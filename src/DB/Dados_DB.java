/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package DB;

import modelos.*;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import static modelos.Constantes.*;

public class Dados_DB
{
    /** Array com os provedores utilizados.  */
    private final ArrayList<Provedor> provedores = new ArrayList();
    /**************************************************************************
     *          Prov 1  Prov 2  Prov 3  (...)   Prov n  Tipo    Grupo
     * PI 1     v11     v12     v13     (...)   v1n     Tipo 1  x
     * PI 2     v21     v22     v23     (...)   v2n     Tipo 2  y
     * PI 3     v31     v32     v33     (...)   v3n     Tipo 3  z
     * ...      ...     ...     ...     (...)   ...     ...     ...
     * PI m     vm1     vm2     vm3     (...)   vmn     Tipo m  m
     * Custo    y1      y2      y3      (...)   yn      LB          0
     ***************************************************************************
     *    #  #  #  #  #  #  #  #  #  #   OU   #  #  #  #  #  #  #  #  #  #
     ***************************************************************************
     *          PI 1    PI 2    PI 3    (...)   PI m    Custo
     * Tipo     Tipo 1  Tipo 2  Tipo 3  (...)   Tipo m  LB
     * Grupo    x       y       z       (...)   m       0
     * Prov 1   v11     v12     v13     (...)   v1n     y1
     * Prov 2   v21     v22     v23     (...)   v2n     y2
     * Prov 3   v31     v32     v33     (...)   v3n     y3
     * ...      ...     ...     ...     (...)   ...     ...
     * Prov n   vn1     vn2     vn3     (...)   vnm     yn     
    **************************************************************************/    
    
    /**
     * 
     * @param provedores 
     */
    public Dados_DB(ArrayList<Provedor> provedores) 
    {
        if(provedores == null || provedores.isEmpty())
            throw new IllegalArgumentException("Entrada vazia!");
                
        for(int i = 0; i < provedores.size(); i++)        
            if(!this.insereProv(provedores.get(i)))
                throw new IllegalArgumentException("O Provedor " + this.provedores.get(i).getNome() + " nao pode ser inserido!");        
    }    
    /**************************************************************************
     * #         Formado do arquivo de dados:    ( n prov x m Pi )
     * (quant prov) OU (nome_Prov1, nome_Prov2, ... , nome_Prov n)(Separados por virgulas) <QUEBRA LINHA>
     * nome_Pi1[grupoPi1], nome_Pi2[grupoPi2], ... , nome_Pi m[grupoPi m], custo           <QUEBRA LINHA>
     * TYPE 1[tipoPi1] (...), TYPE 2[tipoPi2], ... , TYPE m[tipoPi m], LB                  <QUEBRA LINHA>
     ****************************************************************************
     * # Provedor por coluna (PADRAO - default)   (m x n) #
     * v11  <TAB>  v12  <TAB>  v13  <TAB>  (...)   v1n  <QUEBRA LINHA>
     * v21  <TAB>  v22  <TAB>  v23  <TAB>  (...)   v2n  <QUEBRA LINHA>
     * v31  <TAB>  v32  <TAB>  v33  <TAB>  (...)   v3n  <QUEBRA LINHA>
     * ...  <TAB>  ...  <TAB>  ...  <TAB>  (...)   ...  <QUEBRA LINHA>
     * vm1  <TAB>  vm2  <TAB>  vm3  <TAB>  (...)   vmn  <QUEBRA LINHA>      
     * y1   <TAB>  y2   <TAB>  y3   <TAB>  (...)   yn   <QUEBRA LINHA>
     ***************************************************************************
     *    #  #  #  #  #  #  #  #  #  #   OU   #  #  #  #  #  #  #  #  #  #
     ***************************************************************************
     * # Provedor por linha                         (n x m) #
     * (*)v11  <TAB>  v12  <TAB>  v13  <TAB>  (...)   v1m  <TAB>  y1  <QUEBRA LINHA>
     *    v21  <TAB>  v22  <TAB>  v23  <TAB>  (...)   v2m  <TAB>  y2  <QUEBRA LINHA>
     *    v31  <TAB>  v32  <TAB>  v33  <TAB>  (...)   v3m  <TAB>  y3  <QUEBRA LINHA>
     *    ...  <TAB>  ...  <TAB>  ...  <TAB>  (...)   ...  <TAB>  ... <QUEBRA LINHA>
     *    vn1  <TAB>  vn2  <TAB>  vn3  <TAB>  (...)   vnm  <TAB>  yn  <QUEBRA LINHA>
     * # (*) -> Indicador que o input sera lido nesse formato! Provedor por linha
     ***************************************************************************
     * [...] -> Elementos Opcionais
     * # -> Comentario (Comment)
     * grupoPi = 0, 1, 2, ... (Zero: não possui grupo, padrao)
     * tipoPi = HB, LB, NB, HT, LT, HLT (padrao: NB)
     * TYPE = "num" ou "cat" ou "catord" ou "catcomp" (Categorias entre "()" para categoricos, separados por ;)
     * yj = Valores de custos
    **************************************************************************/
    /**
     * Carrega na memória os dados contidos em um arquivo no formato acima e o retorna.
     * @param nomeArq Nome do arquivo com os dados.
     * @return Os dados na memória, null caso algo deu errado.
     */
    public static Dados_DB carregaDados(String nomeArq)
    {
        if(nomeArq == null || nomeArq.isEmpty())
            return null;
        
        try {
            int i, j, numProv, numPis;
            Categoria[] cats;
            String nome, TYPE, tipo, categorias;
            String[] nomesProvs, nomesPis, grupos, stringTipos, valores, vetCats;
            FileReader fr = new FileReader(nomeArq);
            BufferedReader br = new BufferedReader(fr);                    
            ArrayList<Provedor> provedores = new ArrayList();            
            
            // # # # # # PRIMEIRA LINHA: (Quantidade de Provedores) OU (Nome dos Provedores) # # # # #
            String linha = br.readLine();
            try {
                // Quantidade de provedores?
                numProv = Integer.parseInt(linha);
                nomesProvs = new String[numProv]; 
                // Nomes genericos
                for(i = 0; i < numProv; i++)                
                    nomesProvs[i] = "P" + (i + 1);
            }
            catch(NumberFormatException ex)
            {   // Não, nome dos provedores entao ...                
                // Torna a String minúscula e remove qualquer espaçamento nela
                linha = linha.toLowerCase().replaceAll(" ", "").replaceAll("\t", "");  
                // Separa os nomes dos provedores
                nomesProvs = linha.split(",");
                numProv = nomesProvs.length;
            }
            if(numProv <= 0)
                throw new IllegalArgumentException("Nao ha provedores suficientes informados!\n");
            // Adiciona o nome de cada provedor
            for(i = 0; i < numProv; i++)
                provedores.add(new Provedor(nomesProvs[i]));            
            // DEBUG
            /*for(i = 0; i < numProv; i++)
                System.out.print(nomesProvs[i] + " ");
            System.out.println("\n" + numProv); */            
            
            // # # # # # SEGUNDA LINHA: Nome dos PIs [Grupo dos PIs] # # # # #
            linha = br.readLine();
            // Torna a String minúscula e remove qualquer espaçamento nela
            linha = linha.toLowerCase().replaceAll(" ", "").replaceAll("\t", "");  
            // Separa os nomes dos PIs
            nomesPis = linha.split(",");
            numPis = nomesPis.length;
            if(numPis <= 0)
                throw new IllegalArgumentException("Nao ha PIs suficientes informados!\n");
            // Adiciona o nome de cada PI para cada provedor e seu grupo
            grupos = new String[numPis];
            for(i = 0; i < numPis; i++)
            {  
                //System.out.print(nomesPis[i] + " ");
                // NomePI [GrupoPI]
                if(nomesPis[i].contains("[") && nomesPis[i].contains("]"))
                {
                    nome = nomesPis[i].substring(0, nomesPis[i].indexOf('['));
                    if(nome.equals("custo")) // Custo nao tem grupo
                        grupos[i] = "0";
                    else
                        grupos[i] = nomesPis[i].substring(nomesPis[i].indexOf('[') + 1, nomesPis[i].indexOf(']'));                        
                    
                    //System.out.println("Nome: " + nome + "\nGrupo: " + grupo);
                    // Insere esse PI para todo provedor do DB
                    /*for(j = 0; j < numProv; j++)
                    {
                        PI pi = new PI(nome);
                        pi.setGrupo(Integer.parseInt(grupo));
                        // Insere o novo PI no provedor "j"
                        provedores.get(j).inserePI(pi);
                    }*/
                    // Arruma o nome do PI
                    nomesPis[i] = nome;
                }
                else // NomePI -> sem grupo
                {
                    //System.out.println("Nome: " + nomesPis[i]);
                    grupos[i] = "0";
                    // Insere esse PI para todo provedor do DB
                    /*for(j = 0; j < numProv; j++)
                        provedores.get(j).inserePI(new PI(nomesPis[i]));*/
                }
            }
            // DEBUG
            /*for(i = 0; i < nomesPis.length; i++)
                System.out.print(nomesPis[i] + " ");
            System.out.println("\n" + nomesPis.length);  */
            
            // # # # # # TERCEIRA LINHA: TYPE [tipo](categorias) do PI # # # # #
            linha = br.readLine();
            // Torna a String minúscula e remove qualquer espaçamento nela
            linha = linha.toLowerCase().replaceAll(" ", "").replaceAll("\t", ""); 
            // Separa os Tipos dos PIs
            stringTipos = linha.split(","); 
            // Verifica a consistencia da entrada
            if(numPis != stringTipos.length)
                throw new IllegalArgumentException("A quantidade de PIs informados ("
                    + numPis + ") difere da quantidade de tipos de PIs informados "
                    + "(" + stringTipos.length + ")!\n");
            // Adiciona o tipo de cada PI para cada provedor
            for(i = 0; i < stringTipos.length; i++)
            {
                //System.out.print(stringTipos[i] + " ");
                if(nomesPis[i].equals("custo")) // Custo é sempre LB e grupo 0
                {
                    for(j = 0; j < numProv; j++)  // Insercao do custo
                        provedores.get(j).inserePI(new PI_Num("custo", LB, 0));
                    
                    //System.out.println("PI custo inserido!");
                }
                else
                {                    
                    if(stringTipos[i].contains("[") && stringTipos[i].contains("]"))
                    {   // O tipo do PI esta entre "[" e "]"    
                        tipo = stringTipos[i].substring(stringTipos[i].indexOf('[') + 1, stringTipos[i].indexOf(']'));
                        // As categorias do PI estao entre "(" e ")"    
                        if(stringTipos[i].contains("(") && stringTipos[i].contains(")"))
                        {   // Formato: TYPE x[tipoPi x](categorias x)
                            categorias = stringTipos[i].substring(stringTipos[i].indexOf('(') + 1, stringTipos[i].indexOf(')'));
                            TYPE = stringTipos[i].substring(0, Math.min(stringTipos[i].indexOf('['), stringTipos[i].indexOf('(')));
                        }
                        else
                        {   // Formato: TYPE x[tipoPi x]
                            categorias = "";
                            TYPE = stringTipos[i].substring(0, stringTipos[i].indexOf('['));
                        }
                    }
                    else
                    {
                        tipo = "";
                        // As categorias do PI estao entre "(" e ")"    
                        if(stringTipos[i].contains("(") && stringTipos[i].contains(")"))
                        {   // Formato: TYPE x(categorias x)
                            categorias = stringTipos[i].substring(stringTipos[i].indexOf('(') + 1, stringTipos[i].indexOf(')'));
                            TYPE = stringTipos[i].substring(0, stringTipos[i].indexOf('('));
                        }
                        else
                        {   // Formato: TYPE x
                            categorias = "";
                            TYPE = stringTipos[i];
                        }                  
                    }
                    categorias = categorias.toLowerCase().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
                    //System.out.println(categorias);
                    switch(TYPE) 
                    {                    
                        case "num": // Numerico (inteiro ou real)
                            if(tipo.isEmpty())
                            {
                                for(j = 0; j < numProv; j++) // Insercao
                                    provedores.get(j).inserePI(new PI_Num(nomesPis[i], Integer.parseInt(grupos[i])));
                            }
                            else
                            {
                                switch(tipo)
                                {
                                    case "nb":
                                        for(j = 0; j < numProv; j++) // Insercao
                                            provedores.get(j).inserePI(new PI_Num(nomesPis[i], NB, Integer.parseInt(grupos[i])));
                                        break;
                                    case "hb":
                                        for(j = 0; j < numProv; j++) // Insercao
                                            provedores.get(j).inserePI(new PI_Num(nomesPis[i], HB, Integer.parseInt(grupos[i])));
                                        break;
                                    case "lb":
                                        for(j = 0; j < numProv; j++) // Insercao
                                            provedores.get(j).inserePI(new PI_Num(nomesPis[i], LB, Integer.parseInt(grupos[i])));
                                        break;
                                    default:
                                        System.out.println("AVISO: Tipo de PI numerico desconhecido. O padrão NB será utilizado!");
                                        for(j = 0; j < numProv; j++) // Insercao
                                            provedores.get(j).inserePI(new PI_Num(nomesPis[i], Integer.parseInt(grupos[i])));
                                        break;
                                }
                            }
                            break;
                        case "cat": // Categorico nao ordenado (NB)                            
                            vetCats = categorias.split(";");
                            cats = new Categoria[vetCats.length];                            
                            for(j = 0; j < vetCats.length; j++)
                            {
                                cats[j] = new Categoria(vetCats[j]);
                                //System.out.println(cats[j]);
                            }
                            // Insercao
                            for(j = 0; j < numProv; j++)
                                provedores.get(j).inserePI(new PI_Cat(nomesPis[i], Integer.parseInt(grupos[i]), cats, false));
                            break;
                        case "catord": // Categorico ordenado
                            vetCats = categorias.split(";");
                            cats = new Categoria[vetCats.length];
                            for(j = 0; j < vetCats.length; j++)
                            {
                                cats[j] = new Categoria(j + 1, vetCats[j]);
                                //System.out.println(cats[j]);
                            }
                            if(tipo.isEmpty())
                            {
                                for(j = 0; j < numProv; j++) // Insercao
                                    provedores.get(j).inserePI(new PI_Cat(nomesPis[i], Integer.parseInt(grupos[i]), cats, true));
                            }
                            else
                            {
                                switch(tipo)
                                {
                                    case "nb":
                                        for(j = 0; j < numProv; j++) // Insercao
                                            provedores.get(j).inserePI(new PI_Cat(nomesPis[i], Integer.parseInt(grupos[i]), NB, cats, true));
                                        break;
                                    case "ht":
                                        for(j = 0; j < numProv; j++) // Insercao
                                            provedores.get(j).inserePI(new PI_Cat(nomesPis[i], Integer.parseInt(grupos[i]), HT, cats, true));
                                        break;
                                    case "lt":
                                        for(j = 0; j < numProv; j++) // Insercao
                                            provedores.get(j).inserePI(new PI_Cat(nomesPis[i], Integer.parseInt(grupos[i]), LT, cats, true));
                                        break;
                                    case "hlt":
                                        for(j = 0; j < numProv; j++) // Insercao
                                            provedores.get(j).inserePI(new PI_Cat(nomesPis[i], Integer.parseInt(grupos[i]), HLT, cats, true));
                                        break;
                                    default:
                                        System.out.println("AVISO: Tipo de PI categorico desconhecido. O padrão NB será utilizado!");
                                        for(j = 0; j < numProv; j++) // Insercao
                                            provedores.get(j).inserePI(new PI_Cat(nomesPis[i], Integer.parseInt(grupos[i]), cats, true));
                                        break;
                                }
                            }
                            break;
                        case "catcomp": // Categorico composto (NB)                        
                            for(j = 0; j < numProv; j++) // Insercao
                                provedores.get(j).inserePI(new PI_Comp(nomesPis[i], Integer.parseInt(grupos[i]), categorias.split(";")));
                            break;
                        default:
                            throw new IllegalArgumentException("PI de tipo desconhecido (" + TYPE + ").\n");
                    }
                    //System.out.println("Tipo de PI: " + TYPE + "\nGrupo: " + grupos[i] + "\nTipo: " + tipo + "\nCategorias: " + categorias);
                }
            }
            // DEBUG
            /*for(i = 0; i < stringTipos.length; i++)
                System.out.print(stringTipos[i] + " ");
            System.out.println("\n" + stringTipos.length); */  
                                    
            // # # # # # DEMAIS LINHAS: Valores dos PIs # # # # #  
            // Qual o formato do input? Provedor por linha ou coluna?
            linha = br.readLine(); // Le a primeira linha
            linha = linha.toLowerCase().replaceAll(" ", "");
            valores = linha.split("\t");
            j = 0; // Conta linhas restantes
            if(valores[0].charAt(0) == '*') // Provedor por linha
            { // Provedor por linha (cada linha possui todos os valores dos PIs de um provedor)
                //System.out.println("Provedor por linha!");
                valores[0] = valores[0].substring(1); // Remove o asterisco
                // Teste de consistencia
                if(valores.length != numPis)
                    throw new IllegalArgumentException("Quantidade de valores informada ("
                        + valores.length + ") ao provedor " + (j+1) + ", difere da "
                        + "quantidade de PIs passada (" + numPis + ")!\n");
                // Acrescenta os valores da linha (Provedor linha j para todo PI i)
                for(i = 0; i < numPis; i++)
                    provedores.get(j).getPiPos(i).setValor(valores[i]);
                // DEBUG
                /*for(i = 0; i < valores.length; i++)
                    System.out.print(valores[i] + " ");
                System.out.println("\n" + valores.length);*/
                j++;
                // Repete ate o processo para as demais linhas ate o fim dos dados
                while((linha = br.readLine()) != null)
                {
                    linha = linha.toLowerCase().replaceAll(" ", "");
                    valores = linha.split("\t");
                    // Teste de consistencia
                    if(valores.length != numPis)
                        throw new IllegalArgumentException("Quantidade de valores informada ("
                            + valores.length + ") ao provedor " + (j+1) + ", difere da "
                            + "quantidade de PIs passada (" + numPis + ")!\n");
                    // Acrescenta os valores da linha (Provedor linha j para todo PI i)
                    for(i = 0; i < numPis; i++)
                        provedores.get(j).getPiPos(i).setValor(valores[i]);
                    // DEBUG
                    /*for(i = 0; i < valores.length; i++)
                        System.out.print(valores[i] + " ");
                    System.out.println("\n" + valores.length);*/
                    j++;
                }
                // Teste de consistencia
                if(j != numProv)
                    throw new IllegalArgumentException("A quantidade linhas de dados informado ("
                        + j + ") deve ser igual a quantidade de provedores passados (" + numProv + ")!\n");
            }
            else // default
            { // Provedor por coluna (cada linha possui um valor de PI para todos os provedores)
                //System.out.println("Provedor por coluna!");
                if(valores.length != numProv)
                    throw new IllegalArgumentException("Quantidade de valores informada ("
                        + valores.length + ") ao PI " + (j+1) + ", difere da "
                        + "quantidade de provedores passada (" + numProv + ")!\n");
                // Acrescenta os valores da linha (PI linha j para todo provedor i)
                for(i = 0; i < numProv; i++)
                    provedores.get(i).getPiPos(j).setValor(valores[i]);
                // DEBUG
                /*for(i = 0; i < valores.length; i++)
                    System.out.print(valores[i] + " ");
                System.out.println("\n" + valores.length);*/
                j++;
                // Repete ate o processo para as demais linhas ate o fim dos dados
                while((linha = br.readLine()) != null)
                {
                    linha = linha.toLowerCase().replaceAll(" ", "");
                    valores = linha.split("\t");
                    // Teste de consistencia
                    if(valores.length != numProv)
                        throw new IllegalArgumentException("Quantidade de valores informada ("
                            + valores.length + ") ao PI " + (j+1) + ", difere da "
                            + "quantidade de provedores passada (" + numProv + ")!\n");
                    for(i = 0; i < numProv; i++)
                        provedores.get(i).getPiPos(j).setValor(valores[i]);
                    // DEBUG
                    /*for(i = 0; i < valores.length; i++)
                        System.out.print(valores[i] + " ");
                    System.out.println("\n" + valores.length);*/
                    j++;
                }
                // Teste de consistencia
                if(j != numPis)
                    throw new IllegalArgumentException("A quantidade linhas de dados informado ("
                        + j + ") deve ser igual a quantidade de PIs passados (" + numPis + ")!\n");
            }
            br.close();
            // Coloca o PI especial "custo" no lugar correto             
            for(i = 0; i < provedores.size(); i++)
            {
                provedores.get(i).setCusto(Double.parseDouble(provedores.get(i).getPI("custo").getValor()));
                provedores.get(i).removePI("custo");
            }
            return (new Dados_DB(provedores));
        }
        catch (IOException ex)
        {
            System.err.println("Erro ao abrir arquivo " + nomeArq + ": " + ex.getMessage());
            return null;
        }        
    }    
    // GETTERS
    /**
     * Obtem o provedor na posição desejada.
     * @param pos
     * @return 
     */
    public Provedor getProvedorPos(int pos) 
    {        
        return this.provedores.get(pos);
    } 
    /**     
     * @return O numero de provedores do banco de dados.
     */
    public int getNumProvedores() 
    {
        return this.provedores.size();
    } 
    /**
     * Obtém um provedor do banco de dados pelo nome.
     * @param nomeProv Nome do provedor.
     * @return O provedor de nome informado.
     */
    public Provedor getProvedor(String nomeProv) 
    {
        String nome = nomeProv.toLowerCase().replaceAll(" ", "").replaceAll("\t", "");
        for(int i = 0; i < this.provedores.size(); i++)
            if(this.provedores.get(i).getNome().equals(nome))
                return this.provedores.get(i);
        
        return null;
    }
    /**
     * Obtem o indice/posicao de um provedor do banco de dados pelo nome.
     * @param nomeProv Nome do provedor.
     * @return O indice/posicao do provedor passado, -1 caso ele nao exista.
     */
    public int getProvedorPos(String nomeProv)
    {
        String nome = nomeProv.toLowerCase().replaceAll(" ", "").replaceAll("\t", "");
        for(int i = 0; i < this.provedores.size(); i++)
            if(this.provedores.get(i).getNome().equals(nome))
                return i;
        
        return -1;
    }
    /**
     * Obtem-se o tipo do PI de nome passado.
     * @param nomePI Nome do PI.
     * @return O tipo do PI.
     */
    public final int getTipoPI(String nomePI)
    {
        if(nomePI == null || nomePI.isEmpty())
            throw new IllegalArgumentException("Nome de PI invalido!");
        
        String nome = nomePI.toLowerCase().replaceAll(" ", "").replaceAll("\t", "");
        for(int i = 0; i < this.provedores.size(); i++)
            for(int j = 0; j < this.provedores.get(i).getNumPis(); j++)
                if(this.provedores.get(i).getPiPos(j).getNome().equals(nome))
                    return this.provedores.get(i).getPiPos(j).getTipo();
        
        return -1;
    }
    /**
     * Obtem-se o grupo do PI de nome passado.
     * @param nomePI Nome do PI.
     * @return O grupo do PI.
     */
    public final int getGrupoPI(String nomePI)
    {
        if(nomePI == null || nomePI.isEmpty())
            throw new IllegalArgumentException("Nome de PI invalido!");
        
        String nome = nomePI.toLowerCase().replaceAll(" ", "").replaceAll("\t", "");
        for(int i = 0; i < this.provedores.size(); i++)
            for(int j = 0; j < this.provedores.get(i).getNumPis(); j++)
                if(this.provedores.get(i).getPiPos(j).getNome().equals(nome))
                    return this.provedores.get(i).getPiPos(j).getGrupo();
        
        return -1;
    }    
    
    /**
     * Verifica se o provedor de nome passado existe no banco de dados.
     * @param nomeProv Nome do provedor.
     * @return "true" se ele existe, "false", caso contrário.
     */
    public final boolean provExiste(String nomeProv)
    {
        if(nomeProv == null || nomeProv.isEmpty())
            throw new IllegalArgumentException("Nome de provedor invalido!");
        
        String nome = nomeProv.toLowerCase().replaceAll(" ", "").replaceAll("\t", "");
        for(int i = 0; i < this.provedores.size(); i++)
            if(this.provedores.get(i).getNome().equals(nome))
                return true;
        
        return false;
    }
    /**
     * Verifica se o PI de nome passado existe para todo provedor no banco de dados.
     * @param nomePI Nome do PI.
     * @return "true" se ele existe, "false", caso contrário.
     */
    public final boolean piExisteParaTodoProv(String nomePI)
    {
        if(nomePI == null || nomePI.isEmpty())
            throw new IllegalArgumentException("Nome de PI invalido!");
        
        int count = 0;
        String nome = nomePI.toLowerCase().replaceAll(" ", "").replaceAll("\t", "");
        for(int i = 0; i < this.provedores.size(); i++)
            for(int j = 0; j < this.provedores.get(i).getNumPis(); j++)
                if(this.provedores.get(i).getPiPos(j).getNome().equals(nome))
                    count++;
        
        if(count == this.provedores.size())
            return true;
        else
            return false;
    }
    /**
     * Insere um novo provedor no banco de dados.   
     * @param p Provedor a ser inserido.
     * @return "true" se a inserção foi bem sucedida, "false", caso contrário.
     */    
    public final boolean insereProv(Provedor p)
    {
        if(p == null)
            throw new IllegalArgumentException("Provedor inválido!");
        // Não repete nomes
        if(provExiste(p.getNome())) 
            return false;
        
        return this.provedores.add(p);        
    }    
    /**
     * Remove um provedor do banco de dados.  
     * @param nomeProv Nome do provedor.
     * @return "true" se a remoção foi bem sucedida, "false", caso contrário.
     */
    public final boolean removeProv(String nomeProv)
    {
        if(nomeProv == null || !provExiste(nomeProv))
            return false; 
        
        String nome = nomeProv.toLowerCase().replaceAll(" ", "").replaceAll("\t", "");
        for(int i = 0; i < this.provedores.size(); i++)
            if(this.provedores.get(i).getNome().equals(nome))
            {
                this.provedores.remove(i);
                return true;
            }
        
        return false;
    }
    /**
     * Remove todos os PIs da requisição.
     */
    public final void removeTudo()
    {
        for(int i = 0; i < this.provedores.size(); i++)
            this.provedores.get(i).removeTudo();
        this.provedores.clear();       
    }
    /**
     * Retorna o maior valor para o PI de nome passado.
     * @param nomePi O nome do PI.
     * @return O maior valor presente no banco de dados para esse PI.
     */
    public final double getValorMax(String nomePi)
    {
        if(nomePi == null || nomePi.isEmpty())
            throw new IllegalArgumentException("Nome invalido!");
        
        nomePi = nomePi.toLowerCase().replaceAll(" ", "").replaceAll("\t", "");
        if(!piExisteParaTodoProv(nomePi))
            throw new IllegalArgumentException("Esse PI nao pertence a todo provedor dessa base de dados!");
        
        int i, j;
        double atual, maior = 0.00;        
        for(i = 0; i < this.provedores.size(); i++)
        {
            for(j = 0; j < this.provedores.get(i).getNumPis(); j++)
            {
                if(this.provedores.get(i).getPiPos(j).getNome().equals(nomePi))
                {   // Valor pertence ao PI em questão
                    try {
                        atual = Double.parseDouble(this.provedores.get(i).getPiPos(j).getValor());
                    }
                    catch(NumberFormatException ex) {
                        throw new IllegalArgumentException("Essa função é para PIs quantitativos!");
                    }
                    if(atual > maior)
                        maior = atual;
                }
            }
        }
        return maior;
    }
    /**
     * Retorna o menor valor para o PI de nome passado.
     * @param nomePi O nome do PI.
     * @return O menor valor presente no banco de dados para esse PI.
     */
    public final double getValorMin(String nomePi)
    {
        if(nomePi == null || nomePi.isEmpty())
            throw new IllegalArgumentException("Nome invalido!");
        
        nomePi = nomePi.toLowerCase().replaceAll(" ", "").replaceAll("\t", "");
        if(!piExisteParaTodoProv(nomePi))
            throw new IllegalArgumentException("Esse PI nao pertence a todo provedor dessa base de dados!");
        
        int i, j;
        double atual, menor = Double.MAX_VALUE;        
        for(i = 0; i < this.provedores.size(); i++)
        {
            for(j = 0; j < this.provedores.get(i).getNumPis(); j++)
            {
                if(this.provedores.get(i).getPiPos(j).getNome().equals(nomePi))
                {   // Valor pertence ao PI em questão
                    try {
                        atual = Double.parseDouble(this.provedores.get(i).getPiPos(j).getValor());
                    }
                    catch(NumberFormatException ex) {
                        throw new IllegalArgumentException("Essa função é para PIs quantitativos!");
                    }
                    if(atual < menor)
                        menor = atual;
                }
            }
        }
        return menor;
    }   
    /**
     * Retorna o custo do provedor mais caro dessa base de dados.   
     * @return O maior valor presente no banco de dados para esse Indicador.
     */
    public final double getCustoMax()
    {  
        double atual, maior = 0.00;        
        for(int i = 0; i < this.provedores.size(); i++)
        {
            atual = this.provedores.get(i).getCusto();
            if(atual > maior)
                maior = atual;
        }
        return maior;
    }
    
    /**
     * Função que replica o tipo, o grupo e a categorias do PI da requisição com aquele 
     * cadastrado no banco de dados. Replica tambem o peso, a tolerancia
     * e a flag indicando que o PI eh essencial da requisição na base de dados.
     * @param requisicao A requisicao para atualizar os dados.
     */
    public void atualizaDadosDB(Dados_Req requisicao)
    {
        int i, j;
        PI piDB, piReq;            
        for(i = 0; i < requisicao.getNumPis(); i++) 
        {
            piReq = requisicao.getPiPos(i);
            if(!piReq.getNome().equals("custo"))
            {
                for(j = 0; j < this.getNumProvedores(); j++)
                {
                    piDB = this.getProvedorPos(j).getPI(piReq.getNome());
                    if(piDB == null)
                        throw new NullPointerException("O PI " + piReq.getNome() + " nao pode ser encontrado!\n");                    
                    // Atualiza o tipo (Req/DB)
                    if(piReq.getTipo() == DB)  piReq.setTipo(piDB.getTipo()); // Pega o padrao do DB
                    else                       piDB.setTipo(piReq.getTipo()); // Pega o informado na req
                    // Atualiza o grupo (Req)
                    piReq.setGrupo(piDB.getGrupo());
                    // Atualiza o peso do PI (DB)
                    piDB.setPeso(piReq.getPeso());
                    // Atualiza a flag de essencial (DB)
                    piDB.setEssencial(piReq.isEssencial());                    
                    // Atualiza a tolerancia (DB)
                    piDB.setTol(piReq.getTol());
                    // Arruma as categorias desse PI (Req)
                    piReq.insereCategorias(piDB.getCats());
                    // Arruma a ordenacao do categorico simples (Req)
                    piReq.setOrdenado(piDB.isOrdenado());
                }
            }
            else 
                piReq.setTipo(LB);
        }
    }
    
    /** Imprime todos os dados dos PIs e provedores no banco de dados. */
    public void imprimeDados()
    {       
        System.out.println("Dados dos provedores:"); 
        if(!this.provedores.isEmpty())                   
            for(int i = 0; i < this.provedores.size(); i++)
                System.out.println(this.provedores.get(i));        
        else            
            System.out.println("Vazio!");
        
        System.out.println();        
    }    
}