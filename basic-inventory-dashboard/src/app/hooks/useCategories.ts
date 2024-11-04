// src/app/hooks/useCategories.ts
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import api from "@/app/config/api";
import { Category } from "../categorias/page";

// Definindo a interface para a resposta da API
export interface CategoriesResponse {
  categories: Category[];
  totalItems: number;
}

// Definindo a interface para os parâmetros do hook
export interface UseCategoriesParams {
  queryKey: [string, number, number, string, string, string];
  page: number;
  rowsPerPage: number;
  searchQuery: string;
  sortField: string;
  sortDirection: string;
  handleCloseCreateDialog: () => void;
  handleCloseEditDialog: () => void;
  handleCloseDeleteDialog: () => void;
  handleErrorDialog: (message: string) => void;
  handleSuccessDialog: (message: string) => void;
}

const fetchCategories = async (
  page: number,
  rowsPerPage: number,
  searchQuery: string,
  sortField: string,
  sortDirection: string
): Promise<CategoriesResponse> => {
  page = page + 1;
  const response = await api.get(`/categories`, {
    params: {
      page,
      rowsPerPage,
      search: searchQuery,
      sortField,
      sortDirection,
    },
  });
  return response.data;
};

// Função para criar um produto
const createCategoryApi = async (newCategory: Category): Promise<Category> => {
  const response = await api.post<Category>("/categories", newCategory);
  return response.data; // Retorna o produto criado
};

// Função para editar um produto
const editCategoryApi = async (editedCategory: Category): Promise<Category> => {
  const response = await api.put<Category>(
    `/categories/${editedCategory.id}`,
    editedCategory
  );
  return response.data; // Retorna apenas os dados do produto editado
};

// Função para deletar um produto
const deleteCategoryApi = async (categoryId: number): Promise<void> => {
  await api.delete(`/categories/${categoryId}`); // Não precisa retornar nada
};

// Hook
export const useCategories = ({
  queryKey,
  page,
  rowsPerPage,
  searchQuery,
  sortField,
  sortDirection,
  handleCloseCreateDialog,
  handleCloseEditDialog,
  handleCloseDeleteDialog,
  handleErrorDialog,
  handleSuccessDialog,
}: UseCategoriesParams) => {
  const queryClient = useQueryClient();

  const { data, error, isLoading } = useQuery<CategoriesResponse, Error>({
    queryKey: [...queryKey], // Adiciona searchQuery à chave da query
    queryFn: async () =>
      fetchCategories(page, rowsPerPage, searchQuery, sortField, sortDirection),
    staleTime: 60 * 1000, // cache
  });

  // Mutação para criar um produto
  const createCategory = useMutation<Category, Error, Category>({
    mutationFn: createCategoryApi, // Passa a função da API
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["categories"] }); // Invalida a query de produtos
      handleCloseCreateDialog();
      handleSuccessDialog("Categoria criada com sucesso.");
    },
    onError: (error: Error) => {
      console.log("Error creating category:", error);
      handleErrorDialog("Não foi possível criar categoria: " + error.message);
    },
  });
  // Mutação para editar um produto existente
  const editCategory = useMutation<Category, Error, Category>({
    mutationFn: editCategoryApi, // Passa a função da API diretamente

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["categories"] }); // Invalida a query de produtos
      handleCloseEditDialog();
      handleSuccessDialog("Categoria editada com sucesso.");
    },
    onError: (error: Error) => {
      console.log("Error editing category:", error);
      handleErrorDialog("Não foi possível editar a categoria:" + error.message);
    },
  });

  // Mutação para deletar um produto
  const deleteCategory = useMutation<void, Error, number>({
    mutationFn: deleteCategoryApi, // Passa a função da API diretamente

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["categories"] }); // Invalida a query de produtos
      handleCloseDeleteDialog();
      handleSuccessDialog("Categoria deletada com sucesso.");
    },
    onError: (error: Error) => {
      console.log("Error deleting category:", error);
      handleErrorDialog(
        "Não foi possível deletar a categoria: " + error.message
      );
    },
  });

  return {
    data,
    error,
    isLoading,
    createCategory,
    editCategory,
    deleteCategory,
  };
};
