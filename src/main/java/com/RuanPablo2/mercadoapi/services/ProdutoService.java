package com.RuanPablo2.mercadoapi.services;

import com.RuanPablo2.mercadoapi.dtos.ProdutoDTO;
import com.RuanPablo2.mercadoapi.entities.Produto;
import com.RuanPablo2.mercadoapi.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    ProdutoRepository produtoRepository;

    public List<ProdutoDTO> findAll(){
        List<Produto> result = produtoRepository.findAll();
        return result.stream().map(x -> new ProdutoDTO(x)).toList();
    }

    public ProdutoDTO findById(Long id){
        Produto result = produtoRepository.findById(id).orElseThrow(() -> new RuntimeException("Produto não encontrado"));;
        return new ProdutoDTO(result);
    }

    @Transactional
    public ProdutoDTO save(ProdutoDTO dto) {
        Produto produto = new Produto(dto);
        produto = produtoRepository.save(produto);
        return new ProdutoDTO(produto);
    }

    @Transactional
    public ProdutoDTO update(Long id, ProdutoDTO dto) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        produto.atualizarDados(dto);
        return new ProdutoDTO(produtoRepository.save(produto));
    }

    @Transactional
    public void deletar(Long id) {
        produtoRepository.deleteById(id);
    }
}