# WBCLoreCore

**WBCLoreCore** é o plugin central desenvolvido para o servidor **WBCSMP** (Placeholder). Este projeto serve como base para diversas mecânicas personalizadas, lore e funcionalidades exclusivas do servidor.

O objetivo deste plugin é centralizar as modificações de gameplay, garantindo uma experiência coesa e expansível. Novas funcionalidades serão adicionadas conforme o desenvolvimento do servidor avança.

## Funcionalidades Atuais

### 🔮 Portais de Crying Obsidian (Obsidiana Chorona)
Uma mecânica que revitaliza o uso da Crying Obsidian, permitindo que ela seja usada para construir portais funcionais para o Nether.

*   **Construção Personalizada:** A estrutura segue as regras clássicas dos portais (mínimo 2x3, máximo 23x23), mas exige o uso exclusivo de **Crying Obsidian** na moldura.
*   **Ativação:** Utilize um **Isqueiro (Flint and Steel)** na parte interna da moldura para acender o portal.
*   **Conexão Inteligente:**
    *   Teleporte fluido entre Overworld e Nether.
    *   Mantém a proporção de coordenadas 1:8.
    *   Gera portais de saída automaticamente e salva suas localizações para conexões consistentes.

### 🏰 Strongholds
Alterações na geração das fortalezas para se adequar à lore do servidor.

*   **Remoção de Portais do End:** Os portais do End são removidos automaticamente das Strongholds assim que os chunks são gerados, impedindo o acesso tradicional ao The End por meio delas.

### 🎒 Ender Chests Aprimorados
Melhoria no sistema de baús do fim para expandir o armazenamento dos jogadores.

*   **Capacidade Dobrada:** Ender Chests agora possuem **54 slots** (tamanho de um baú duplo), ao invés dos 27 slots padrão.
*   **Armazenamento Seguro:** Os itens são salvos em arquivos individuais por jogador, garantindo persistência.
*   **Proteção Anti-Dupe:** Sistema de salvamento atômico implementado para prevenir perda de itens ou duplicação em caso de falhas no servidor.

---

*Mais funcionalidades e mecânicas de lore serão documentadas aqui conforme forem implementadas.*

## Requisitos

*   Servidor Minecraft (Spigot, Paper, Purpur, etc.)
*   Versão do Minecraft: **1.21+**
*   Java 21 ou superior

## Instalação

1.  Baixe o arquivo `.jar` mais recente na aba de Releases.
2.  Coloque o arquivo na pasta `plugins` do seu servidor.
3.  Reinicie o servidor para carregar o plugin.
4.  Arquivos de configuração necessários (como `portals.yml`) serão gerados automaticamente.

## Desenvolvimento

Este projeto utiliza **Maven** para gerenciamento de dependências e build.

Para compilar o projeto localmente:

```bash
mvn clean package
```

## 📄 Licença

Desenvolvido por **Desenvolvedores do WEBB CENTER**.
Todos os direitos reservados ao servidor WEBB CENTER.
