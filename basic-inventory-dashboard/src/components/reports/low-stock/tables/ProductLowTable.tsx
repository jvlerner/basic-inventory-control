// components/ProductTable.tsx
"use client";

import React from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  TablePagination,
} from "@mui/material";
import { Product } from "@/app/produtos/page";

interface ProductTableProps {
  products: Product[];
  page: number;
  count: number;
  rowsPerPage: number;
  handleChangePage: (event: unknown, newPage: number) => void;
  handleChangeRowsPerPage: (event: React.ChangeEvent<HTMLInputElement>) => void;
}

const ProductLowTable: React.FC<ProductTableProps> = ({
  products,
  page,
  rowsPerPage,
  count,
  handleChangePage,
  handleChangeRowsPerPage,
}) => {
  return (
    <TableContainer component={Paper}>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell sx={{ fontWeight: 600 }}>ID</TableCell>
            <TableCell sx={{ fontWeight: 600 }}>Nome</TableCell>
            <TableCell sx={{ fontWeight: 600 }}>Descrição</TableCell>
            <TableCell sx={{ fontWeight: 600 }}>Preço</TableCell>
            <TableCell sx={{ fontWeight: 600 }}>Quantidade</TableCell>
            <TableCell sx={{ fontWeight: 600 }}>Categoria</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {products.map((product) => (
            <TableRow key={product.id}>
              <TableCell sx={{ minWidth: "50px" }}>{product.id}</TableCell>
              <TableCell sx={{ minWidth: "250px" }}>{product.name}</TableCell>
              <TableCell sx={{ minWidth: "350px" }}>
                {product.description ? product.description : "Não possui."}
              </TableCell>
              <TableCell style={{ width: "100px" }}>
                {product.price?.toFixed(2)}
              </TableCell>
              <TableCell style={{ width: "100px" }}>
                {product.quantity}
              </TableCell>
              <TableCell style={{ width: "250px" }}>
                {product.category?.name}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
      <TablePagination
        rowsPerPageOptions={[10, 25, 50, 100]}
        component="div"
        count={count}
        rowsPerPage={rowsPerPage}
        page={page}
        onPageChange={handleChangePage}
        onRowsPerPageChange={handleChangeRowsPerPage}
      />
    </TableContainer>
  );
};

export default ProductLowTable;