[![Build Status](https://travis-ci.org/d-klotz/intelli-ponto-api.svg?branch=master)](https://travis-ci.org/d-klotz/intelli-ponto-api)

# IntelliPonto
API do sistema IntelliPonto, este sistema processa e armazena os registros de entrada e saída de um funcionário em uma determinada empresa. 

# Ferramentas Utilizadas
- Java
- Spring Boot
- JPA e Spring Data
- FlyWay
- TravisCI
- Swagger
- JWT authentication
- EhCache

# Executando o projeto
- Na raiz do projeto execute o comando **java -jar target\IntelliPonto-0.0.1-SNAPSHOT.jar**

# Atenticação JWT
- Crie o registro de pessoa através das APIs **/api/cadastrar-pf** ou **/api/cadastrar-pj**
- Utilize a API **/auth** passando o email e a senha do usuario criado
- Para utilizar o restante das APIs, passe o token gerado na propriedade Authorization no header da requisição no formato "Bearer **tokenGerado**"

Ou

Utilize o registro de pessoa criado durante a execução do projeto com a api **/auth** conforme abaixo:

*{ "email": "admin@intelliPonto.com",*
  *"senha": "123456" }*
  
e utilize o token gerado na propriedade Authorization no header da requisição no formato "Bearer **tokenGerado**"

# Acesso a documentação do projeto
- Para acessar a documentação das APIs acesse a API /swagger-ui.html
