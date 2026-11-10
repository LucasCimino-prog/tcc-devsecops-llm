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
```
## Principais Resultados

**Qualidade Estrutural:** Todos os modelos apresentaram **Rating A** em Manutenibilidade e Confiabilidade (0 bugs lógicos e 0 *code smells* impeditivos).

**Falsos Positivos de Framework:** A varredura identificou 5 alertas de severidade **High** associados à desativação do CSRF. Como a API implementa uma arquitetura **Stateless** baseada em JWT (Bearer Tokens), estes apontamentos foram classificados tecnicamente como falsos positivos do analisador estático.

**Desempenho do Gemini 3.8 Flash:** O modelo demonstrou alta consistência sintática e estrutural, gerando código limpo e funcional compatível com o ecossistema Spring Boot. Na Fase 2 (Informada), adotou o armazenamento em memória (H2) e aplicou o Spring Security padrão, compartilhando dos mesmos alertas estruturais de framework identificados nos demais modelos.

**Maturidade Arquitetural (GPT-6 Luna):** Demonstrou viés de prontidão para produção (**Production-Readiness**), implementando espontaneamente um banco de dados real (PostgreSQL) e **Database as Code** (Flyway).

**Defesa em Profundidade vs. Novas Superfícies (Claude 5.5 Sonnet):** O modelo Claude gerou o artefato mais seguro contra **BOLA** ao criar rotas centralizadas (`/me`) e logs de auditoria para tentativas de acesso cruzado. No entanto, a passagem direta da variável de ID para o console gerou um alerta de **Log Injection** (CWE-117, severidade **Low**), ilustrando como a implementação de defesas complexas pode introduzir vetores de risco colaterais se a sanitização de entradas não for aplicada de ponta a ponta.

## Instruções de Execução Local

Para rodar os módulos localmente e realizar chamadas com o Insomnia:

**Pré-requisitos:** JDK 17, Apache Maven 3.9+ e PostgreSQL (exigido pelo módulo do GPT).

Clone este repositório:

```bash
git clone [https://github.com/LucasCimino-prog/tcc-devsecops-llm.git](https://github.com/LucasCimino-prog/tcc-devsecops-llm.git)
cd tcc-devsecops-llm
```

## Licença e Ética Acadêmica

Este repositório foi criado para fins estritamente acadêmicos e de pesquisa (Resolução CNS n.º 510/2016). Os trechos de código presentes nos subdiretórios foram gerados integralmente pelos respectivos modelos de Inteligência Artificial para fins de avaliação estática, configurando os próprios objetos de estudo da pesquisa, sem utilização de código proprietário de terceiros.

## Histórico de Interações (Prompts e Chats)

Para fins de auditoria, transparência e reprodutibilidade da pesquisa, os links públicos das sessões de engenharia de prompt utilizadas para gerar os artefatos de cada modelo estão listados abaixo:

| Modelo LLM | Fase 1 (Geração Base / Ingênua) | Fase 2 (Geração Segura / Informada) |
| :--- | :--- | :--- |
| **Gemini 3.8 Flash** | [https://share.gemini.google/OXqCXtrmgdWu](URL_DO_CHAT_GEMINI_FASE_1) | [https://share.gemini.google/pHE9Wj0DHwJQ](URL_DO_CHAT_GEMINI_FASE_2) |
| **GPT-6 Luna** | [https://chatgpt.com/share/6aee08ee-5518-83e9-b693-5472011f7917](URL_DO_CHAT_GPT_FASE_1) | [https://chatgpt.com/share/6aef5786-812c-83e9-97aa-f125a1beaf25](URL_DO_CHAT_GPT_FASE_2) |
| **Claude 5.5 Sonnet** | [https://claude.ai/share/63082785-db1c-4526-9416-89ec09aa47d6](URL_DO_CHAT_CLAUDE_FASE_1) | [https://claude.ai/share/a9c25edc-2d3d-452e-9d93-0141923f807b](URL_DO_CHAT_CLAUDE_FASE_2) |