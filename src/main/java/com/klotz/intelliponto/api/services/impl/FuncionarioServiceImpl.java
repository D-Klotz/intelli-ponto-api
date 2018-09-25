package com.klotz.intelliponto.api.services.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.klotz.intelliponto.api.entities.Funcionario;
import com.klotz.intelliponto.api.repositories.FuncionarioRepository;
import com.klotz.intelliponto.api.services.FuncionarioService;

@Service
public class FuncionarioServiceImpl implements FuncionarioService {
	
	private static final Logger log = LoggerFactory.getLogger(FuncionarioService.class);
	
	@Autowired
	FuncionarioRepository funcionarioRepository;

	public Funcionario persistir(Funcionario funcionario) {
		log.info("Persistindo o funcionario {}", funcionario);
		return this.funcionarioRepository.save(funcionario);
	}

	public Optional<Funcionario> buscarPorCpf(String cpf) {
		log.info("Buscando funcionario por cpf {}", cpf);
		return Optional.ofNullable(this.funcionarioRepository.findByCpf(cpf));
	}

	public Optional<Funcionario> buscarPorEmail(String email) {
		log.info("Buscando funcionario por email {}", email);
		return Optional.ofNullable(this.funcionarioRepository.findByEmail(email));
	}
	
	public Optional<Funcionario> buscarPorId(Long id) {
		log.info("Buscando funcionario por id {}", id);
		return Optional.ofNullable(this.funcionarioRepository.findOne(id));
	}

	
}
