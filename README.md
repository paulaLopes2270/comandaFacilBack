# Comanda Fácil

## Overview

**Projeto:** Comanda Fácil

**Descrição:**  
O projeto "Comanda Fácil" é uma aplicação desenvolvida em Java utilizando Spring Boot para gestão de reservas de mesas em restaurantes. O sistema permite que empresas gerenciem mesas, cardápios e reservas de clientes de forma integrada e eficiente. Os usuários podem se cadastrar como empresas ou clientes, sendo que as empresas podem gerenciar suas mesas, cardápios e reservas, enquanto os clientes podem fazer reservas para as mesas disponíveis.

**Tecnologias Utilizadas:**
- **Java 17**
- **Spring Boot**
- **JPA/Hibernate**
- **Jakarta Persistence (JPA)**
- **Lombok**
- **Spring Security**
- **H2 Database**

## Estrutura do Projeto

Abaixo, uma visão geral da estrutura de pacotes:

- **`com.comandaFacil.comandaFacil.model`**: Contém as classes de modelo (entidades) que representam as tabelas do banco de dados.
- **`com.comandaFacil.comandaFacil.repository`**: Contém as interfaces dos repositórios para acesso aos dados.
- **`com.comandaFacil.comandaFacil.service`**: Contém as classes de serviço que implementam a lógica de negócios.
- **`com.comandaFacil.comandaFacil.response`**: Contém classes que formatam as respostas da API.
- **`com.comandaFacil.comandaFacil`**: Contém a classe principal que inicializa a aplicação.

## Descrição das Classes e Interfaces

### Modelos (Entities)

#### Reserva
- **Descrição:** Representa uma reserva de mesa no sistema.
- **Atributos:**
  - `id`: Identificador único da reserva.
  - `dataHora`: Data e hora da reserva.
  - `numeroPessoas`: Número de pessoas para a reserva.
  - `area`: Área do restaurante onde a reserva foi feita.
  - `mesa`: Referência à mesa reservada.
  - `empresa`: Referência à empresa (restaurante) onde a reserva foi feita.
  - `usuario`: Referência ao cliente que fez a reserva.

#### Usuario
- **Descrição:** Representa um usuário do sistema (empresa ou cliente).
- **Atributos:**
  - `id`: Identificador único do usuário.
  - `username`: Nome de usuário único.
  - `password`: Senha do usuário.
  - `role`: Papel do usuário no sistema (EMPRESA ou CLIENTE).
  - `email`: Email do usuário.
  - `telefone`: Telefone do usuário.
  - `empresas`: Lista de empresas associadas ao usuário (caso ele seja uma empresa).

### Repositórios

#### EmpresaRepository
- **Descrição:** Interface para realizar operações de CRUD na entidade `Empresa`.
- **Métodos principais:**
  - `findAllByUsuario(Usuario usuario)`: Retorna todas as empresas associadas a um usuário específico.

#### MenuRepository
- **Descrição:** Interface para realizar operações de CRUD na entidade `Menu`.
- **Métodos principais:**
  - `findByEmpresaId(Long empresaId)`: Retorna todos os menus associados a uma empresa específica.

#### MesaRepository
- **Descrição:** Interface para realizar operações de CRUD na entidade `Mesa`.
- **Métodos principais:**
  - `findByEmpresa(Empresa empresa)`: Retorna todas as mesas associadas a uma empresa específica.

#### ReservaRepository
- **Descrição:** Interface para realizar operações de CRUD na entidade `Reserva`.
- **Métodos principais:**
  - `findByEmpresaAndDataHoraBetween(Empresa empresa, LocalDateTime startDateTime, LocalDateTime endDateTime)`: Retorna as reservas de uma empresa em um intervalo de tempo.
  - `findReservasByMesaAndHorario(Mesa mesa, LocalDateTime inicio, LocalDateTime fim)`: Retorna reservas para uma mesa específica em um horário específico.

#### UsuarioRepository
- **Descrição:** Interface para realizar operações de CRUD na entidade `Usuario`.
- **Métodos principais:**
  - `findByUsername(String username)`: Retorna um usuário pelo nome de usuário.

### Serviços

#### EmpresaService
- **Descrição:** Implementa a lógica de negócios relacionada às empresas.
- **Métodos principais:**
  - `saveEmpresa(Empresa empresa)`: Salva uma nova empresa.
  - `updateEmpresa(Long id, Empresa empresaDetails)`: Atualiza os detalhes de uma empresa existente.
  - `deleteEmpresa(Long id)`: Exclui uma empresa existente.

#### MenuService
- **Descrição:** Implementa a lógica de negócios relacionada aos menus.
- **Métodos principais:**
  - `saveMenu(Menu menu)`: Salva um novo menu.
  - `updateMenu(Long id, Menu menuDetails)`: Atualiza os detalhes de um menu existente.
  - `deleteMenu(Long id)`: Exclui um menu existente.

#### MesaService
- **Descrição:** Implementa a lógica de negócios relacionada às mesas.
- **Métodos principais:**
  - `saveMesa(Mesa mesa)`: Salva uma nova mesa.
  - `updateMesa(Long mesaId, Mesa mesaDetails)`: Atualiza os detalhes de uma mesa existente.
  - `toggleOcupacaoMesa(Long mesaId)`: Alterna o estado de ocupação manual de uma mesa.

#### ReservaService
- **Descrição:** Implementa a lógica de negócios relacionada às reservas.
- **Métodos principais:**
  - `saveReserva(Reserva reserva)`: Salva uma nova reserva, verificando a disponibilidade da mesa.
  - `deleteReserva(Long reservaId)`: Exclui uma reserva existente.
  - `liberarMesasOcupadas()`: Libera mesas que foram ocupadas manualmente após a data de ocupação.

#### UsuarioService
- **Descrição:** Implementa a lógica de negócios relacionada aos usuários, incluindo autenticação e registro.
- **Métodos principais:**
  - `registerUsuario(Usuario usuario)`: Registra um novo usuário com senha criptografada.
  - `loadUserByUsername(String username)`: Carrega um usuário pelo nome de usuário para autenticação.

### Respostas

#### JwtAuthenticationResponse
- **Descrição:** Representa a resposta da autenticação JWT contendo o token gerado.

### Aplicação Principal

#### ComandaFacilApplication
- **Descrição:** Classe principal que inicializa a aplicação Spring Boot.

## Funcionalidades Implementadas

- **Gerenciamento de Empresas:** Empresas podem ser criadas, atualizadas e excluídas.
- **Gerenciamento de Mesas:** Mesas podem ser criadas, atualizadas, excluídas e marcadas como ocupadas manualmente.
- **Gerenciamento de Menus:** Menus podem ser criados, atualizados e excluídos.
- **Reservas de Mesas:** Clientes podem reservar mesas, com verificação de disponibilidade e ocupação manual.
- **Autenticação de Usuários:** Sistema de autenticação utilizando JWT com roles diferenciadas (EMPRESA e CLIENTE).

## Execução do Projeto

Para executar o projeto localmente, siga os passos abaixo:

### Pré-requisitos:
- JDK 17 ou superior instalado.
- Maven instalado.

### Passos:
1. Clone o repositório:  
   `git clone https://github.com/paulaLopes2270/comandaFacilBack`
   
2. Navegue até o diretório do projeto:  
   `cd comandaFacil`
   
3. Execute o comando:  
   `mvn spring-boot:run`

