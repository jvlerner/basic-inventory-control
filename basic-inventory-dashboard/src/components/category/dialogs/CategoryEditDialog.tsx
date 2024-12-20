"use client";

import React, { useEffect } from "react";
import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Button,
  IconButton,
} from "@mui/material";
import { useForm, Controller } from "react-hook-form";
import { Category } from "@/app/categorias/page";
import CloseIcon from '@mui/icons-material/Close';
import { categoryMaxSize } from "@/config/maxSize";

interface CategoryEditDialogProps {
  open: boolean;
  onClose: () => void;
  category: Category;
  onEdit: (editedCategory: Category) => void;
}

const CategoryEditDialog: React.FC<CategoryEditDialogProps> = ({
  open,
  onClose,
  category,
  onEdit,
}) => {
  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<Category>();

  useEffect(() => {
    if (category) {
      reset(category);
    }
  }, [category, reset]);

  const onSubmit = (data: Category) => {
    onEdit(data);
    onClose();
  };

  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>
        Editar Categoria
        <IconButton
          aria-label="fechar"
          onClick={onClose}
          style={{ position: "absolute", right: 8, top: 8 }}
        >
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent>
        <form onSubmit={handleSubmit(onSubmit)}>
          <Controller
            name="name"
            control={control}
            rules={{
              required: "Nome é obrigatório",
              maxLength: {
                value: categoryMaxSize.name || 50,
                message: `Nome deve ter no máximo ${categoryMaxSize.name.toString()} caracteres`,
              },
            }}
            render={({ field }) => (
              <TextField
                {...field}
                label="Nome*"
                variant="outlined"
                error={!!errors.name}
                helperText={errors.name ? errors.name.message : ""}
                fullWidth
                margin="dense"
              />
            )}
          />

          <Controller
            name="description"
            control={control}
            rules={{
              maxLength: {
                value: categoryMaxSize.description || 100,
                message: `Descrição deve ter no máximo ${categoryMaxSize.description.toString()} caracteres`,
              },
            }}
            render={({ field }) => (
              <TextField
                {...field}
                label="Descrição"
                variant="outlined"
                multiline
                rows={4}
                error={!!errors.description}
                helperText={
                  errors.description ? errors.description.message : ""
                }
                fullWidth
                margin="dense"
              />
            )}
          />

          <DialogActions>
            <Button onClick={onClose} color="primary">
              Cancelar
            </Button>
            <Button type="submit" color="primary">
              Salvar
            </Button>
          </DialogActions>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default CategoryEditDialog;
