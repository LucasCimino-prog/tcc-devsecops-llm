cat << 'EOF' > evidencias/README.md
# Pacote de Evidências Experimentais - TCC DevSecOps

Este diretório armazena os artefatos brutos, relatórios de análise estática (SAST) e evidências de execução gerados durante o experimento comparativo entre as LLMs (**Gemini 3.8 Flash**, **GPT-6 Luna** e **Claude 5.5 Sonnet**).

---

## Estrutura das Evidências

### 1. `sast-sonarcloud/`
Contém as capturas de tela e exportações de relatórios do painel web do **SonarCloud**.

* **Resultados Consolidados da Varredura:**
    * **Total de Arquivos Analisados:** 33 arquivos Java (Ecossistema Spring Boot 3.x).
    * **Total de Apontamentos (Issues/Hotspots):** 6 apontamentos.
    * **Alertas de CSRF (`SecurityConfig.java`):** 5 alertas de severidade *High*.
        * **Classificação:** Falso Positivo (Arquitetura REST Stateless com Bearer Token JWT).
    * **Alerta de Log Injection (`UserService.java` - Claude Informado):** 1 alerta de severidade *Low* (CWE-117).
        * **Classificação:** Vulnerabilidade Real decorrente de falta de sanitização na entrada do parâmetro de auditoria contra BOLA.

---

### 2. `testes-insomnia/`
Contém a coleção de requisições exportada do Insomnia (`Insomnia_Collection.json`) e evidências de teste de endpoint:

* **Fase 1 (Geração Base):** Testes de rotas `/api/users/{id}` (Identificação de vulnerabilidade BOLA / OWASP API1).
* **Fase 2 (Geração Segura):**
    * **Gemini / GPT / Claude:** Validação de cadastro (`201 Created`) e login (`200 OK`).
    * **GPT / Claude:** Validação da rota `/api/users/me` (Mitigação de BOLA por design).
    * **Claude:** Teste da resposta de antienumeração (`404 Not Found` em `/api/users/{id}`).

---

### 3. `tabelas-metricas/`
Planilha em Excel (`metricas-seguranca-tcc.xlsx`) contendo a tabulação descritiva dos dados da Fase 1 e Fase 2 para os gráficos do Capítulo 4 (Resultados e Discussão).

EOF