# Avaliação de Segurança em Códigos Gerados por LLMs: Uma Abordagem DevSecOps

[![Java 17](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-336791.svg)](https://www.postgresql.org/)
[![SonarCloud](https://img.shields.io/badge/SonarCloud-SAST-orange.svg)](https://sonarcloud.io/)
[![GitHub Actions](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-2088FF.svg)](https://github.com/features/actions)

Repositório oficial do Trabalho de Conclusão de Curso (TCC) desenvolvido por Lucas Cimino Alves. Este projeto avalia e compara a eficácia de segurança de três Modelos de Linguagem de Grande Escala (LLMs) — **Gemini 3.8 Flash**, **GPT-6 Luna** e **Claude 5.5 Sonnet** — na geração de APIs RESTful estruturadas para o ecossistema Spring Boot, validadas através de varreduras SAST em uma esteira de Integração Contínua (CI).

## Objetivos

Analisar a capacidade de diferentes ferramentas de Inteligência Artificial em incorporar requisitos de segurança (*Security by Design*) e mitigar vulnerabilidades mapeadas pelo **OWASP API Security Top 10** (como BOLA e *Mass Assignment*), simulando um fluxo real de desenvolvimento na cultura DevSecOps.

## Metodologia e Fases do Experimento

O experimento foi conduzido por meio de engenharia de prompts, dividido em duas fases sucessivas de submissão aos modelos:

1. **Fase 1 (Geração Base - Ingênua):** Solicitação de criação de uma API de gestão de usuários sem fornecer requisitos explícitos de segurança, estabelecendo a linha de base de comportamento dos modelos.
2. **Fase 2 (Geração Segura - Informada):** Solicitação do mesmo artefato, porém instruindo os modelos a aplicarem validação rigorosa de entradas, mitigação contra BOLA (Broken Object Level Authorization), criptografia de senhas (BCrypt) e o uso do Spring Security.

Os 33 arquivos-fonte gerados foram compilados e avaliados de forma isonômica pelo **SonarCloud** na esteira do **GitHub Actions**.

## Arquitetura e Tecnologias

* **Linguagem:** Java 17
* **Framework:** Spring Boot 3.x, Spring Security, Spring Data JPA
* **Banco de Dados:** H2 Database (em memória) e PostgreSQL (com Flyway para versionamento, proposto pelo GPT)
* **CI/CD:** GitHub Actions (compilação remota isolada via `mvn compile -DskipTests`)
* **Análise Estática (SAST):** SonarCloud / SonarScanner
* **Validação de Endpoints:** Insomnia (testes locais de rotas e injeção de tokens JWT)

## Estrutura do Repositório

O projeto está modularizado no Apache Maven, abrigando os códigos exatos gerados pelas IAs para garantir a integridade dos dados coletados:

```text
tcc-devsecops-llm/
├── .github/workflows/          # Configurações da esteira CI/CD (build.yml / sonar.yml)
├── evidencias/                 # Pacote probatório da pesquisa
│   ├── propostas-refatoracao/  # Aprimoramentos do autor sobre o código gerado
│   ├── sast-sonarcloud/        # JSONs brutos e capturas de tela do painel do SonarCloud
│   ├── tabelas-metricas/       # Planilhas tabuladas com os resultados comparativos
│   ├── testes-git-bash/        # Coleções de teste dentro do Git Bash para os prompts ingenuôs.
│   └── testes-insomnia/        # Coleções de teste de API (.json) exportadas
├── prompt-ingenuo-gemini/      # (Fase 1) Código gerado pelo Gemini
├── prompt-ingenuo-gpt/         # (Fase 1) Código gerado pelo GPT
├── prompt-ingenuo-claude/      # (Fase 1) Código gerado pelo Claude
├── prompt-informado-gemini/    # (Fase 2) Código gerado pelo Gemini
├── prompt-informado-gpt/       # (Fase 2) Código gerado pelo GPT (com PostgreSQL)
├── prompt-informado-claude/    # (Fase 2) Código gerado pelo Claude (Defesa em Profundidade)
└── pom.xml                     # Orquestrador multi-módulo Maven