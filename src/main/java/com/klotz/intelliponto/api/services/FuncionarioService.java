package com.klotz.intelliponto.api.services;

import java.util.Optional;

import com.klotz.intelliponto.api.entities.Funcionario;

public interface FuncionarioService {

	/**
	 * Persiste um usuario na base de dados
	 * 
	 * @param funcionario
	 * @return Funcionario
	 */
	Funcionario persistir(Funcionario funcionario);
	
	/**
	 * Busca e retorna um funcionario pelo cpf
	 * 
	 * @param cpf
	 * @return Funcionario
	 */
	Optional<Funcionario> buscarPorCpf(String cpf);
	
	/**
	 * Busca um funcionario pelo email
	 * 
	 * @param email
	 * @return Funcionario
	 */
	Optional<Funcionario> buscarPorEmail(String email);
	
	/**
	 * Busca um funcionario pelo id
	 * 
	 * @param Id
	 * @return Funcionario
	 */
	Optional<Funcionario> buscarPorId(Long Id);
	
}
