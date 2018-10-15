package com.klotz.intelliponto.api.controllers;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klotz.intelliponto.api.dtos.CadastroPFDto;
import com.klotz.intelliponto.api.entities.Empresa;
import com.klotz.intelliponto.api.entities.Funcionario;
import com.klotz.intelliponto.api.enums.PerfilEnum;
import com.klotz.intelliponto.api.response.Response;
import com.klotz.intelliponto.api.services.EmpresaService;
import com.klotz.intelliponto.api.services.FuncionarioService;
import com.klotz.intelliponto.api.utils.PasswordUtils;

@RestController
@RequestMapping("/api/cadastrar-pf")
@CrossOrigin(origins= "*")
public class CadastroPFController {
	
	private static final Logger log = LoggerFactory.getLogger(CadastroPFController.class);
	
	@Autowired
	private EmpresaService empresaService;
	
	@Autowired
	private FuncionarioService funcionarioService;

	private CadastroPFController() {	
	}
	
	/**
	 * Cadastra um funcionario pessoa fisica no sistema
	 * 
	 * @param result
	 * @return ResponseEntity<Response<CadastroPFDto>>
	 * @throws NoSuchAlgorithmException
	 */
	@PostMapping
	public ResponseEntity<Response<CadastroPFDto>> cadastrar(@Valid @RequestBody CadastroPFDto cadastroPFDto,
			BindingResult result) throws NoSuchAlgorithmException {
		
		log.info("Cadastrando PF: {}", cadastroPFDto.toString());
		Response<CadastroPFDto> response = new Response<CadastroPFDto>();
		
		validarDadosExistentes(cadastroPFDto, result);
		Funcionario funcionario = this.converterDtoParaFuncionario(cadastroPFDto, result);
		
		if (result.hasErrors()) {
			log.error("Erro validando dados de cadastro PF: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(cadastroPFDto.getCnpj());
		empresa.ifPresent(emp -> funcionario.setEmpresa(emp));
		this.funcionarioService.persistir(funcionario);
		
		response.setData(this.converterCadastroPFDto(funcionario));
		return ResponseEntity.ok(response);
		
		
	}

	/**
	 * Popula o DTO de cadastro com os dados do funcionario e empresa
	 * 
	 * @param funcionario
	 * @return
	 */
	private CadastroPFDto converterCadastroPFDto(Funcionario funcionario) {
		CadastroPFDto cadastroPFDto = new CadastroPFDto();
		cadastroPFDto.setId(funcionario.getId());
		cadastroPFDto.setNome(funcionario.getNome());
		cadastroPFDto.setEmail(funcionario.getEmail());
		cadastroPFDto.setCpf(funcionario.getCpf());
		cadastroPFDto.setCnpj(funcionario.getEmpresa().getCnpj());
		funcionario.getQtdHorasAlmocoOpt().ifPresent(qtdHorasAlmoco -> cadastroPFDto
				.setQtdHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmoco))));
		funcionario.getQtdHorasTrabalhoDiaOpt().ifPresent(qtdHorasTrabalho -> cadastroPFDto
				.setQtdTrabalhoHorasDia(Optional.of(Float.toString(qtdHorasTrabalho))));
		funcionario.getValorHoraOpt()
				.ifPresent(valorHora -> cadastroPFDto.setValorHora(Optional.of(valorHora.toString())));
		
		return cadastroPFDto;
	}

	/**
	 * Converte os dados do DTO para funcionario
	 * @param cadastroPFDto
	 * @param result
	 * @return Funcionario
	 * @throws NoSuchAlgorithmException
	 */
	private Funcionario converterDtoParaFuncionario(CadastroPFDto cadastroPFDto, BindingResult result) 
			throws NoSuchAlgorithmException {
		 Funcionario funcionario = new Funcionario();
		 
		 funcionario.setNome(cadastroPFDto.getNome());
		 funcionario.setCpf(cadastroPFDto.getCpf());
		 funcionario.setEmail(cadastroPFDto.getEmail());
		 funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
		 funcionario.setSenha(PasswordUtils.gerarBCrypt(cadastroPFDto.getSenha()));
		 cadastroPFDto.getQtdHorasAlmoco()
		 	.ifPresent(qtdHorasAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));
		 cadastroPFDto.getQtdTrabalhoHorasDia()
		 	.ifPresent(qtdHorastrabalhoDia -> funcionario.setQtdHorasTrabalhoDia(Float.valueOf(qtdHorastrabalhoDia)));
		 cadastroPFDto.getValorHora()
		 	.ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));
		 
		 return funcionario;
	}

	private void validarDadosExistentes(CadastroPFDto cadastroPFDto, BindingResult result) {
		Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(cadastroPFDto.getCnpj());
		if (!empresa.isPresent()) {
			result.addError(new ObjectError("empresa", "Empresa não cadastrada"));			
		}
		
		this.funcionarioService.buscarPorCpf(cadastroPFDto.getCpf())
			.ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF já existente")));
		
		this.funcionarioService.buscarPorEmail(cadastroPFDto.getEmail())
			.ifPresent(func -> result.addError(new ObjectError("funcionario", "Email já existente")));
	}
}
