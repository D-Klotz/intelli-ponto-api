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
-

# Atenticação JWT
- Crie o registro de pessoa através das APIs api/cadastrar-pf ou api/cadastrar-pj
- Utilize a API /auth passando o email e a senha do usuario criado
- Para utilizar o restante das APIs, passe o JWT token gerado na propriedade Authorization no header da requisição no formato "Bearer <JWT Token> 

# Acesso a documentação do projeto
- Para acessar a documentação das APIs acesse a API /swagger-ui.html
