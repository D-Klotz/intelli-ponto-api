package com.klotz.intelliponto.api.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.klotz.intelliponto.api.entities.Empresa;
import com.klotz.intelliponto.api.services.EmpresaService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EmpresaControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private EmpresaService empresaService;
	
	private static final String BUSCAR_EMPRESA_CNPJ_URL = "/api/empresas/cnpj/";
	private static final Long ID = Long.valueOf(1);
	private static final String CNPJ = "51463645000100";
	private static final String RAZAO_SOCIAL = "Empresa XYZ";
	
	@Test
	@WithMockUser
	public void testBuscarEmpresaCnpjInvalido() throws Exception {
		BDDMockito.given(this.empresaService.buscarPorCnpj(Mockito.anyString())).willReturn(Optional.empty());
		
		mockMvc.perform(MockMvcRequestBuilders.get(BUSCAR_EMPRESA_CNPJ_URL + CNPJ).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors").value("Empresa não encontrada para o CNPJ:" + CNPJ));
	}
	
	@Test
	@WithMockUser
	public void testBuscarEmpresaCnpjValido() throws Exception {
		BDDMockito.given(this.empresaService.buscarPorCnpj(Mockito.anyString()))
				.willReturn(Optional.of(this.buscarDadosEmpresa()));
		
		mockMvc.perform(MockMvcRequestBuilders.get(BUSCAR_EMPRESA_CNPJ_URL + CNPJ)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(ID))
				.andExpect(jsonPath("$.data.razaoSocial").value(RAZAO_SOCIAL))
				.andExpect(jsonPath("$.data.cnpj").value(CNPJ))
				.andExpect(jsonPath("$.errors").isEmpty());		
	}

	private Empresa buscarDadosEmpresa() {
		Empresa empresa = new Empresa();
		empresa.setCnpj(CNPJ);
		empresa.setId(ID);
		empresa.setRazaoSocial(RAZAO_SOCIAL);
		return empresa;
	}
	
	
}
