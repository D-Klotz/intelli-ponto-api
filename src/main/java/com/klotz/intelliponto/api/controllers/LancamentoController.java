package com.klotz.intelliponto.api.controllers;

import java.awt.image.DirectColorModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import javax.print.attribute.standard.RequestingUserName;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.klotz.intelliponto.api.dtos.LancamentoDto;
import com.klotz.intelliponto.api.entities.Funcionario;
import com.klotz.intelliponto.api.entities.Lancamento;
import com.klotz.intelliponto.api.response.Response;
import com.klotz.intelliponto.api.services.FuncionarioService;
import com.klotz.intelliponto.api.services.LancamentoService;

@RestController
@RequestMapping("/api/lancamento")
@CrossOrigin(origins="*")
public class LancamentoController {

	private static final Logger log = LoggerFactory.getLogger(LancamentoController.class);
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired
	private LancamentoService lancamentoService;
	
	@Autowired
	private FuncionarioService funcionarioService;
	
	@Value("${paginacao.qtd_por_pagina}")
	private int qtdPorPagina;
	
	public LancamentoController() {
	}
	
	/**
	 * Retorna a listagem de lançamentos de um funcionario
	 * 
	 * @param funcionarioId
	 * @return ResponseEntity<Response<LancamentoDto>>
	 */
	@GetMapping(value="/funcionario/{funcionarioId}")
	public ResponseEntity<Response<Page<LancamentoDto>>> listarPorFuncionarioId(
			@PathVariable("funcionarioId") Long funcionarioId,
			@RequestParam(value="pagina", defaultValue="0") int pagina,
			@RequestParam(value="ordenacao", defaultValue="id") String ordenacao,
			@RequestParam(value="direction", defaultValue="DESC") String direction){
	
		log.info("Buscando lançamentos por Id do funcionario: {}, pagina: {}", funcionarioId, pagina);
		Response<Page<LancamentoDto>> response = new Response<Page<LancamentoDto>>();
				
		PageRequest pageRequest = new PageRequest(pagina, this.qtdPorPagina, Direction.valueOf(direction), ordenacao);
		Page<Lancamento> lancamentos = this.lancamentoService.buscarPorFuncionarioId(funcionarioId, pageRequest);
		Page<LancamentoDto> lancamentosDto = lancamentos.map(lancamento -> this.converterLancamentosDto(lancamento));
		
		response.setData(lancamentosDto);
		return ResponseEntity.ok(response);		
	}
	
	/**
	 * Retorna um lancamento por Id
	 * 
	 * @param id
	 * @return ResponseEntity<Response<LancamentoDto>>
	 */
	@GetMapping(value="/{id}")
	public ResponseEntity<Response<LancamentoDto>> listarPorId(@PathVariable("id") Long id){
		log.info("Busncando lancamaento por id: {}", id);
		Response<LancamentoDto> response = new Response<LancamentoDto>();
		Optional<Lancamento> lancamento = this.lancamentoService.buscarPorId(id);
		
		if (!lancamento.isPresent()) {
			log.info("Lancamento não encontrado para o id: {}", id);
			response.getErrors().add("Lancamento não encontrado para o id: {}" + id);
			return ResponseEntity.badRequest().body(response);
		}
		
		response.setData(this.converterLancamentosDto(lancamento.get()));
		return ResponseEntity.ok(response);
	}
	
	/**
	 * Adiciona um novo lancamento
	 * 
	 * @param lancamento
	 * @param result
	 * @return ResponseEntity<Response<LancamentoDto>>
	 * @throws ParseException
	 */
	@PostMapping
	public ResponseEntity<Response<LancamentoDto>> adicionar(@Valid @RequestBody LancamentoDto lancamentoDto,
			BindingResult result) throws ParseException {
		log.info("Adicionando Lancamento: {}", lancamentoDto.toString());
		Response<LancamentoDto> response = new Response<LancamentoDto>();
		this.validarFuncionario(lancamentoDto, result);
		Lancamento lancamento = this.converterDtoParaLancamento(lancamentoDto, result);
		
		if(result.hasErrors()) {
			log.error("Erro validando lancamento: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		lancamento = this.lancamentoService.persistir(lancamento);
		response.setData(this.converterLancamentosDto(lancamento));
		return ResponseEntity.ok(response);
	}
	
	/**
	 * Atualiza os dados de um lancamento
	 * 
	 * @param id
	 * @param lancamentoDto
	 * @return ResponseEntity<Response<LancamentoDto>>
	 * @throws ParseException
	 *//*
	@PostMapping(value="/{id}")
	public ResponseEntity<Response<LancamentoDto>> atualizar(@PathVariable("id") Long id,
			@Valid @RequestBody LancamentoDto lancamentoDto, BindingResult result) throws ParseException {
		
		
	}*/
	
	/**
	 * Converte um lancamentoDTO para um lancamento
	 * 
	 * @param lancamentoDto
	 * @param result
	 * @return Lancamento
	 */
	private Lancamento converterDtoParaLancamento(LancamentoDto lancamentoDto, BindingResult result) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Valida um funcionario, verificando se ele é existente
	 * e valido no sistema.
	 * 
	 * @param lancamentoDto
	 * @param result
	 */
	private void validarFuncionario(LancamentoDto lancamentoDto, BindingResult result) {

		if (lancamentoDto.getFuncionarioId() == null) {
			result.addError(new ObjectError("funcionario", "Funcionário não informado"));
			return;
		}
		
		log.info("Validando funcionario id: {}", lancamentoDto.getFuncionarioId());
		Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(lancamentoDto.getFuncionarioId());
		
		if (!funcionario.isPresent()) {
			result.addError(new ObjectError("funcionario", "Funcionario não encontrado, Id inexistente."));
		}
		
	}

	/**
	 * Converte uma entidade Lancamento para o seu respectivo DTO.
	 * 
	 * @param lancamento
	 * @return LancamentoDto
	 */
	public LancamentoDto converterLancamentosDto(Lancamento lancamento) {
		LancamentoDto lancamentoDto = new LancamentoDto();
		lancamentoDto.setId(Optional.of(lancamento.getId()));
		lancamentoDto.setData(this.dateFormat.format(lancamento.getData()));
		lancamentoDto.setTipo(lancamento.getTipo().toString());
		lancamentoDto.setDescricao(lancamento.getDescricao());
		lancamentoDto.setLocalizacao(lancamento.getLocalizacao());
		lancamentoDto.setFuncionarioId(lancamento.getFuncionario().getId());
		return lancamentoDto;
	}
}
