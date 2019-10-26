/*
 * UDESC - Universidade do Estado de Santa Catarina
 * Aluno: Lucas Borges de Moraes
 * Curso: Mestrado em Computação Aplicada - MCA 
 * Orientadores: Adriano Fiorese e Rafael Parpinelli
 */
package aplicacao;


import DB.*;
import modelos.*;
import modelosMetaheuristicas.*;
import deterministicos.*;
import metaHeuristicas.*;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;
import static modelos.Constantes.*;

public abstract class TestaMetodos 
{
    /** Gerador randomico da classe. */
    private static final Random gerador = new Random();
    /**
     * Roda os Algoritmos.
     * @param args [Quant. prov.] [Num. Req.] [Num. PIs] [Num. DB]
     */
    public static void main(String args[]) //throws InterruptedException
    {              
        // # # # # # # # # # # # # # # # # TESTES # # # # # # # # # # # # # # # #
        /*Dados_DB db = Dados_DB.carregaDados("Dados/Base8/DADOS2016x10.txt");
        db.imprimeDados();*/
        /*int req = 8;
        double[] vals = diminuiCusto(100, 6, 3, afetados[req - 1]);
        for(i = 0; i < vals.length; i++)
            System.out.println(vals[i]);
        Thread.sleep(20000);
        //double[] vals = {0.5, 1.25};
        desfazDiminuiCusto(100, 6, 3, afetados[req - 1], vals);*/
        
        //boolean[] cod = {false, false, true, false, false, false, false, false, false, false};
        //af.fitness(new IndividuoBin(1, cod));
        
        //System.out.println(db.piExisteParaTodoProv("ram"));        
        //System.out.println(db.getTipoPI("custo"));
        
        //long tempoInicial, tempoFinal;
        //Individuo ind = new IndividuoBin(1, 10);
        //ind.geraCodificacaoRandomica();
        //AvaliaFitness af = new AvaliaFitness("Dados/DADOS10x5.txt", "Requisicoes/REQ10x5_4.txt");
        //af.imprime();
        //tempoInicial = System.currentTimeMillis();  
        //af.fitness(ind);
        //tempoFinal = (System.currentTimeMillis() - tempoInicial);
        //System.out.println("\nTempo de execução: " + tempoFinal + " ms");
        
        /*int TAM_POP = 30;
        Populacao p = new Populacao(TAM_POP, 1);  
        
        for(int i = 0; i < (TAM_POP/10); i++)
        {
            p.getIndividuos()[10 * i + 0].setFitness(1.50);
            p.getIndividuos()[10 * i + 1].setFitness(1.25);
            p.getIndividuos()[10 * i + 2].setFitness(3.00);
            p.getIndividuos()[10 * i + 3].setFitness(2.50);
            p.getIndividuos()[10 * i + 4].setFitness(2.75);
            p.getIndividuos()[10 * i + 5].setFitness(2.80);
            p.getIndividuos()[10 * i + 6].setFitness(4.50);
            p.getIndividuos()[10 * i + 7].setFitness(2.5);
            p.getIndividuos()[10 * i + 8].setFitness(1.75);
            p.getIndividuos()[10 * i + 9].setFitness(3.20);
        }
        for(int i = 0; i < TAM_POP; i++)
            System.out.println("IndividuoBin " + (i+1) + " fitness: " + p.getIndividuos()[i].getFitness());  				      
       
        EscalonamentoLinear.escalonamento(p, 2.0);
        for(int i = 0; i < TAM_POP; i++)
            System.out.println("IndividuoBin " + (i+1) + " fitness escalonado: " + p.getIndividuos()[i].getFitness());*/
        
        /*ArrayList<boolean[]> indsCods = new ArrayList();
        indsCods.add(new boolean[]{true,true,true,true,true});
        indsCods.add(new boolean[]{false,false,false,false,false});
        indsCods.add(new boolean[]{true,false,false,false});
        indsCods.add(new boolean[]{true,false,true,false,true});
        indsCods.add(new boolean[]{false,false,false,true});
        //indsCods.add(new boolean[]{true,true,false,false,false});
        //indsCods.add(new boolean[]{});
        PopulacaoBin pop = new PopulacaoBin(5,5,indsCods);
        pop.imprimePopulacao();*/
        /*ArrayList<double[]> indsCods = new ArrayList();
        indsCods.add(new double[]{0,1,2,3,4});
        indsCods.add(new double[]{1,0,1,0,0});
        //indsCods.add(new double[]{-1,1,6,7,8});
        indsCods.add(new double[]{0,0,0,0});
        //indsCods.add(new double[]{1,1.5,3,7,8});
        indsCods.add(new double[]{10,2,1,4,32});
        indsCods.add(new double[]{});
        PopulacaoReal pop = new PopulacaoReal(5,5,-10,10,indsCods);
        pop.imprimePopulacao();*/
        
        // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
        // Teste do tempo medio de uma avaliacao de finess (EA)
        /*AvaliaFitness af;
        long tempoInicial, tempoFinal;
        String pathDB, pathReq;
        int i, j, numAval = 100000;
        IndividuoBin[] indsBin = new IndividuoBin[numAval];
        IndividuoReal[] indsRe = new IndividuoReal[numAval];
        //int[] provs = {10, 20, 30, 50, 100, 200};
        int numReq, numProv, totReqs = 5;
        int numDB = 7;
        int numPis = 4;        
        
        System.out.println("Para " + numAval + " avaliacoes...");
        //for(i = 0; i < provs.length; i++)
        {
            numProv = 61;//provs[i];
            System.out.println("Com " + numProv + " provedores:");
            for(j = 0 ; j < numAval; j++)
            {
                indsBin[j] = new IndividuoBin(numProv);
                indsRe[j] = new IndividuoReal(numProv);
            }
            pathDB = "Dados/Base" + numDB + "/DADOS" + numProv + "x" + numPis + ".txt";
            for(numReq = 1; numReq <= totReqs; numReq++)
            {
                System.out.println("\tRequisicao " + numReq + ":");
                pathReq = "Requisicoes/Base" + numDB + "/REQ" + numReq + "_" + numPis + ".txt";
                af = new AvaliaFitness(pathDB, pathReq);
                //af.imprime();
                for(j = 0 ; j < numAval; j++)
                {
                    indsBin[j].geraCodRand();
                    indsRe[j].geraCodRand(-1.0, 1.0);
                }
                
                // Binario
                tempoInicial = System.currentTimeMillis();
                for(j = 0; j < numAval; j++)
                    af.fitness(indsBin[j]);
                tempoFinal = (System.currentTimeMillis() - tempoInicial);
                System.out.println("\t\tTempo de execução(Bin): " + (double)tempoFinal/numAval + " ms");

                // Real
                tempoInicial = System.currentTimeMillis();
                for(j = 0; j < numAval; j++)
                    af.fitness(indsRe[j]);
                tempoFinal = (System.currentTimeMillis() - tempoInicial);
                System.out.println("\t\tTempo de execução(Real): " + (double)tempoFinal/numAval + " ms");
            }
        }*/
        
        // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
        // Teste da curva de temperatura do SA usando graficos em R
        /*try {
            int MAXIT = 50;
            FileWriter fw = new FileWriter("tempsSA.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            
            bw.append("itSA <- c(seq(0, " + MAXIT + "));");
            bw.newLine();
            bw.append("tempSA <- c(");
            for(i = 0; i < MAXIT; i++)
                bw.append(SA.decTemp(i, 1.0, 0.0, MAXIT) + ",");
            bw.append(SA.decTemp(i, 1.0, 0.0, MAXIT) + ");");
            bw.newLine();
            bw.newLine();
            bw.append("pdf(\"../Desktop/SA_Temp.pdf\");");
            bw.newLine();
            bw.append("plot(tempSA ~ itSA, type='l', col='blue', xlab='Iterations', "
                    + "ylab='Temperature', ylim=c(0,1), "
                    + " main='SA temperature curve');");
            bw.newLine();
            bw.append("dev.off();");
            bw.newLine();
            bw.close();            
        }
        catch(IOException ex)
        {
            System.out.println("Erro ao escrever no arquivo: " + ex.getMessage());
        }*/
        
        /*Dados_DB db = Dados_DB.carregaDados("Dados/Base" + numDB + "/DADOS" + 200 + "x" + numPis + ".txt");
        boolean[] s = retCodProv("P1", db);
        for(i = 0; i < s.length; i++)
            System.out.print(s[i] + " ");
        System.out.println();*/
        
        // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
        // # # # # # # # # # # # # # # # # TESTES # # # # # # # # # # # # # # # 
        // # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
        // Metodos
        Matching mt;      DEA dea;      AlgExaustivo exaustivo;  
        SA sa;            AG ag;        EDB edb;                   EDD edd;
        
        int i, j; // Iteradores
        // PARAMETROS MATCHING
        int n = 10;
        double C1 = 0.999;
        double C3 = 0.7;
        // PARAMETROS DEA
        double C = C3;
        // # # # PARAMETROS DAS METAHEURISTICAS : SA / AG / EDB / EDD # # #
        // * * * PARAMETROS COMUNS * * *
        int maxExecucoes = 30;
        int maxGeracoes = 2000; // AG / EDB / EDD
        int tamPop = 50;        // AG / EDB / EDD
        int probPertub = 50;    // EDB / EDD
        boolean[] s0;
        boolean rel = false;
        // PARAMETROS SA
        int maxIt = 100000;
        int cteMetr = 500;
        // PARAMETROS AG
        int probCross = 95;
        int probMutAG = 3;
        // PARAMETROS EDB
        int probMut = 5;
        // PARAMETROS EDD
        double F = 0.5;
        // # # # FIM PARAMETROS # # #
        
        // Guarda as respostas dos metodos
        ArrayList<Provedor> eleitosMtc, eleitosDEA;
        ArrayList<boolean[]> eleitosSA, eleitosHibridoSA;
        ArrayList<IndividuoBin> eleitosAG, eleitosEDB, eleitosEDD, eleitosExaustivo, eleitosOpt;
        
        // Para usar diminuicao de custos DB 3
        //                   (P8)(P6 P8)(P1 P6 P7)(P1 P2)(P6)(P3)(P5)(P1 P6)
        int[][] afetados = {{8},{6,8},{1,6,7},  {1,2}, {6},{3},{5},{1,6}}; // Dim custos
        // Para usar diminuicao de custos DB 6
        //                 (P5)(P8)(P2 P4)(P2 P3)(P5 P6 P8)
        //int[][] afetados = {{5},{8},{2,4},{2,3}, {5,6,8}}; // Dim custos
        
        // Top deterministicos DB 3: P8 (Req. 1), P6 (Req. 2), P7 (Reqs. 3 and 4), P6 (Req. 5), P3 (Req. 6), P5 (Req. 7), P1 (Req. 8)
        // Top deterministicos DB 6: P5 (Req. 1), P8 (Req. 2), P4 (Req. 3), P3 (Req. 4), P5 (Req. 5)
        double[] vals = null;
        
        // # # # PARA TESTES SIMPLES # # #
        // Com numeros constantes de provedores, PIs, numero da base, requisicao
        int numProv = 200; // DB 7 = 61; DB 8 = 672 
        int numDB = 3;
        int numPis = 5;   // DB 1,2,3 = 5; DB 4,5,6 = 4; DB 7 = 4 (so qt) ou 5 (qt e ql); DB 8 = 10
        int numReqs = 8;  // DB 1,2,4,5,6,7,8 = 5; DB 3 = 8; DB 8 = 6
        int req = 8;
        boolean dimCusto = true; // sempre falso pra DB 7 e 8
                
        //Dados_DB database = Dados_DB.carregaDados("Dados/Base" + numDB + "/DADOS" + numProv + "x" + numPis + ".txt");
        
        for(req = 1; req <= numReqs; req++)
        {
            if(dimCusto && numDB < 7) vals = diminuiCusto(numProv, numPis, numDB, afetados[req - 1]);
            
            // DETERMINISTICOS
            //mt = new Matching(numProv, req, numPis, numDB, C1, C3);
            //eleitosMtc = mt.rodaMatching(n); // mt.rodaMatching(n);   mt.rodaMatching(n);   mt.rodaMatching(n);   mt.rodaMatching(n);
            //for(j = 0; j < eleitosMtc.size(); j++)  System.out.println(eleitosMtc.get(j).getNome()); System.out.println();
                        
            //dea = new DEA(numProv, req, numPis, numDB, C);
            //eleitosDEA = dea.rodaDEA();
            //for(j = 0; j < eleitosDEA.size(); j++)  System.out.println(eleitosDEA.get(j).getNome()); System.out.println();
                        
            // METAHEURISTICOS
            //s0 = null;
            //sa = new SA(numProv, req, numPis, numDB, maxExecucoes, maxIt, cteMetr);
            //eleitosSA = sa.rodaSA(rel, s0);   //sa.rodaSA(rel, s0);   //sa.rodaSA(rel, s0);   sa.rodaSA(rel, s0);   sa.rodaSA(rel, s0);
            //for(j = 0; j < eleitosSA.size(); j++)  imprimeProvCod(eleitosSA.get(j)); System.out.println();
                        
            //ag = new AG(numProv, req, numPis, numDB, maxExecucoes, maxGeracoes, tamPop, probCross, probMutAG); 
            //eleitosAG = ag.rodaAG(rel, null);   //ag.rodaAG(rel, null);   //ag.rodaAG(rel, null);   ag.rodaAG(rel, null);   ag.rodaAG(rel, null);
            //for(j = 0; j < eleitosAG.size(); j++)  eleitosAG.get(j).imprimeProvCod(); System.out.println();
                        
            //edb = new EDB(numProv, req, numPis, numDB, maxExecucoes, maxGeracoes, tamPop, probPertub, probMut);
            //eleitosEDB = edb.rodaEDB(rel, null);   //edb.rodaEDB(rel, null);   edb.rodaEDB(rel, null);   edb.rodaEDB(rel, null);   edb.rodaEDB(rel, null);
            //for(j = 0; j < eleitosEDB.size(); j++)  eleitosEDB.get(j).imprimeProvCod(); System.out.println();            
            
            //edd = new EDD(numProv, req, numPis, numDB, maxExecucoes, maxGeracoes, tamPop, probPertub, F);  
            //eleitosEDD = edd.rodaEDD(rel, null);   //edd.rodaEDD(rel, null);   //edd.rodaEDD(rel, null);   edd.rodaEDD(rel, null);   edd.rodaEDD(rel, null);
            //for(j = 0; j < eleitosEDD.size(); j++)  eleitosEDD.get(j).imprimeProvCod(); System.out.println();            
            
            //Opt opt = new Opt(numProv, req, numPis, numDB, maxExecucoes, maxIt);
            //eleitosOpt = opt.rodaOpt(null);
            //for(j = 0; j < eleitosOpt.size(); j++)  eleitosOpt.get(j).imprimeProvCod(); System.out.println();
                    
            // HIBRIDOS
            /*mt = new Matching(numProv, req, numPis, numDB, C1, C3);
            eleitosMtc = mt.rodaMatching(n);
            s0 = retCodProv(eleitosMtc.get(0).getNome(), database);
            sa = new SA(numProv, req, numPis, numDB, maxExecucoes, maxIt, cteMetr);
            eleitosHibridoSA = sa.rodaSA(rel, s0);
            for(j = 0; j < eleitosHibridoSA.size(); j++)  imprimeProvCod(eleitosHibridoSA.get(j)); System.out.println();
            */
            
            edd = new EDD(numProv, req, numPis, numDB, maxExecucoes, maxGeracoes/2, tamPop, probPertub, F);  
            eleitosEDD = edd.rodaEDD(rel, null);
            Opt opt = new Opt(numProv, req, numPis, numDB, maxExecucoes, maxIt/2);
            eleitosOpt = opt.rodaOpt(eleitosEDD.get(gerador.nextInt(eleitosEDD.size())).getCod());
            for(j = 0; j < eleitosOpt.size(); j++)  eleitosOpt.get(j).imprimeProvCod(); System.out.println();
            
            // EXAUSTIVO - Impraticavel para mais de 30 CPs
            /*exaustivo = new AlgExaustivo(numProv, req, numPis, numDB);         
            eleitosExaustivo = exaustivo.rodaExaustivo();
            //for(j = 0; j < eleitosExaustivo.size(); j++)  eleitosExaustivo.get(j).imprimeProvCod(); System.out.println();
            */
            
            if(dimCusto && numDB < 7) desfazDiminuiCusto(numProv, numPis, numDB, afetados[req - 1], vals);
        }
        
        // # # # # # # # # # # # # # # # EXAUSTIVO # # # # # # # # # # # # # # #
        /*int[] numProvsEx = {10, 20}; 
        int[] numProvsCustoEx = {20}; // Dim custos - Exautivo
        AlgExaustivo exaustivo;
        System.out.println("# # # # # # # # # # Inicio das Execuçoes # # # # # # # # # #\n"); 
        // Para o exautivo
        for(i = 0; i < numProvsEx.length; i++)
        {
            for(j = 0; j < numReqs; j++)
            {  
                exaustivo = new AlgExaustivo(numProvsEx[i], (j+1), numPis, numDB);         
                exaustivo.rodaExaustivo();
            }
        }    
        System.out.println("# # # # # # # # # # Com Diminuiçao de Custos # # # # # # # # # #\n");        
        for(i = 0; i < numProvsCustoEx.length; i++)
        {
            for(j = 0; j < numReqs; j++)
            {
                vals = diminuiCusto(numProvsCustoEx[i], numPis, numDB, afetados[j]);                
                exaustivo = new AlgExaustivo(numProvsCustoEx[i], (j+1), numPis, numDB);
                exaustivo.rodaExaustivo();                
                desfazDiminuiCusto(numProvsCustoEx[i], numPis, numDB, afetados[j], vals);
            }
        }*/
        
        // # # # # # # # # # # # # # # # # # SA # # # # # # # # # # # # # # # # #
        /*        
        //for(int SAreq = 1; SAreq <= numReqs; SAreq++)
        {
            // # # # # # Manipulacao da solucao inicial # # # # #
            // Matching: P8 (Req. 1), P6 (Req. 2), P7 (Req. 3), P7 (Req. 4), P6 (Req. 5), P3 (Req. 6), P5 (Req. 7), P1 (Req. 8).
            switch(SAreq) {
                case 1:  s0 = retCodProv("P8", database);  break;
                case 2:  s0 = retCodProv("P6", database);  break;
                case 3:  s0 = retCodProv("P7", database);  break;
                case 4:  s0 = retCodProv("P7", database);  break;
                case 5:  s0 = retCodProv("P6", database);  break;
                case 6:  s0 = retCodProv("P3", database);  break;
                case 7:  s0 = retCodProv("P5", database);  break;
                case 8:  s0 = retCodProv("P1", database);  break;                
            }
            //vals = diminuiCusto(SAprovs, 5, numDB, afetados[SAreq - 1]);            
            sa = new SA(SAprovs, SAreq, 5, numDB, maxExSA, maxItSA, cteMetr);
            sa.rodaSA(rel, s0);   //sa.rodaSA(rel, s0);   sa.rodaSA(rel, s0);   sa.rodaSA(rel, s0);   sa.rodaSA(rel, s0);             
            //desfazDiminuiCusto(SAprovs, 5, numDB, afetados[SAreq - 1], vals);
        }*/
        
        // # # # # # # # # # # CASOS SEM DIMINUIÇAO DE CUSTOS # # # # # # # # # #
        /*System.out.println("# # # # # # # # # # Inicio das Execuçoes # # # # # # # # # #\n");
        //int[] numProvs = {10, 100, 200};        
        //int[] numProvsCusto = {100, 200}; // Dim custos
        /*
        // Casos com 5 PIs
        for(i = 0; i < numProvs.length; i++)
        {
            for(j = 0; j < numReqs; j++)
            {                
                ag = new AG(numProvs[i], (j+1), numPis, numDB, maxExecucoes, maxGeracoes, tamPop, probCross, probMutAG);    ag.rodaAG(false);
                edb = new EDB(numProvs[i], (j+1), numPis, numDB, maxExecucoes, maxGeracoes, tamPop, probPertub, probMut);   edb.rodaEDB(false);
                edd = new EDD(numProvs[i], (j+1), numPis, numDB, maxExecucoes, maxGeracoes, tamPop, probPertub, F);         edd.rodaEDD(false);                
            }
        }*/        
        
        // # # # # # # # # # # CASOS COM DIMINUIÇAO DE CUSTOS # # # # # # # # # #
        /*System.out.println("# # # # # # # # # # Com Diminuiçao de Custos # # # # # # # # # #\n"); 
        // Casos com 5 PIs        
        for(i = 0; i < numProvsCusto.length; i++)
        {
            for(j = 0; j < numReqs; j++)
            {
                vals = diminuiCusto(numProvsCusto[i], numPis, numDB, afetados[j]);
                ag = new AG(numProvsCusto[i], (j+1), numPis, numDB, maxExecucoes, maxGeracoes, tamPop, probCross, probMutAG);   ag.rodaAG(false);                
                edb = new EDB(numProvsCusto[i], (j+1), numPis, numDB, maxExecucoes, maxGeracoes, tamPop, probPertub, probMut);  edb.rodaEDB(false);
                edd = new EDD(numProvsCusto[i], (j+1), numPis, numDB, maxExecucoes, maxGeracoes, tamPop, probPertub, F);        edd.rodaEDD(false);
                desfazDiminuiCusto(numProvsCusto[i], numPis, numDB, afetados[j], vals);
            }
        }*/
                       
        // # # # # # # # # # # # # # # AG / EDB / EDD # # # # # # # # # # # # # #
        //int[] numProvs = {10, 100, 200};        
        //int[] numProvsCusto = {100, 200}; // Dim custos
        /*        
        // # # # # # PARA TESTES COMPLEXOS # # # # #
                
        // # # # # # # # # # # CASOS SEM DIMINUIÇAO DE CUSTOS # # # # # # # # # #
        /*System.out.println("# # # # # # # # # # Inicio das Execuçoes # # # # # # # # # #\n"); 
        // Casos com 5 PIs
        for(i = 0; i < numProvs.length; i++)
        {
            for(j = 0; j < numReqs; j++)
            {                
                ag = new AG(numProvs[i], (j+1), numPis, numDB, maxExecucoes, maxGeracoes, tamPop, probCross, probMutAG);    ag.rodaAG(false);
                edb = new EDB(numProvs[i], (j+1), numPis, numDB, maxExecucoes, maxGeracoes, tamPop, probPertub, probMut);   edb.rodaEDB(false);
                edd = new EDD(numProvs[i], (j+1), numPis, numDB, maxExecucoes, maxGeracoes, tamPop, probPertub, F);         edd.rodaEDD(false);                
            }
        }*/        
        
        // # # # # # # # # # # CASOS COM DIMINUIÇAO DE CUSTOS # # # # # # # # # #
        /*System.out.println("# # # # # # # # # # Com Diminuiçao de Custos # # # # # # # # # #\n"); 
        // Casos com 5 PIs        
        for(i = 0; i < numProvsCusto.length; i++)
        {
            for(j = 0; j < numReqs; j++)
            {
                vals = diminuiCusto(numProvsCusto[i], numPis, numDB, afetados[j]);
                ag = new AG(numProvsCusto[i], (j+1), numPis, numDB, maxExecucoes, maxGeracoes, tamPop, probCross, probMutAG);   ag.rodaAG(false);                
                edb = new EDB(numProvsCusto[i], (j+1), numPis, numDB, maxExecucoes, maxGeracoes, tamPop, probPertub, probMut);  edb.rodaEDB(false);
                edd = new EDD(numProvsCusto[i], (j+1), numPis, numDB, maxExecucoes, maxGeracoes, tamPop, probPertub, F);        edd.rodaEDD(false);
                desfazDiminuiCusto(numProvsCusto[i], numPis, numDB, afetados[j], vals);
            }
        }*/
        // # # # # # # # # FIM DOS CASOS COM DIMINUIÇÂO DE CUSTOS # # # # # # # #
        
    }
    // #########################################################################
    /**
     * Gera a codificacao binaria correspondente ao nome do provedores passado
     * utilizando a base de dados dada.
     * @param nomeProv Nome identificador unico do provedor.
     * @param database Base de dados que contem o nome desse provedor.
     * @return Codificacao binaria do provedor de nome e base passadas, null se ele nao existe.
     */
    private static boolean[] retCodProv(String nomeProv, Dados_DB database)
    {
        boolean[] cod;
        int i, pos, numProvs = database.getNumProvedores();        
        
        pos = database.getProvedorPos(nomeProv);
        if(pos == -1)
            return null;
        
        cod = new boolean[numProvs];
        for(i = 0; i < numProvs; i++)
            cod[i] = false;
        cod[pos] = true;
        return cod;
    }    
    // #########################################################################
    /**
     * 
     * @param numProv
     * @param numPIs
     * @param numDB
     * @param provAfetados
     * @return 
     */
    private static double[] diminuiCusto(int numProv, int numPIs, int numDB, int[] provAfetados)
    {
        if(provAfetados.length <= 0)
            throw new IllegalArgumentException("diminuiCusto: Numero dos provedores são invalidos");
                
        int i;
        long ponteiro;
        double custo;
        double[] valAntes = new double[provAfetados.length];
        String linha, nomeArq = "Dados/Base" + numDB + "/DADOS" + numProv + "x" + numPIs + ".txt";
        String[] valores;
        try {   
            File arq = new File(nomeArq);
            RandomAccessFile file = new RandomAccessFile(arq, "rw");            
            // Primeira linha: nome dos provedores, ignora
            file.readLine();
            // Segunda linha: nome dos PIs, conta quantos PIs tem
            linha = file.readLine();            
            valores = linha.split(",");
            // Terceira linha: tipos dos PIs, ignora
            file.readLine();
            // Outras linhas: Valores, uma linha por PI
            for(i = 0; i < valores.length - 1; i++)
                file.readLine(); // Ignora todas as linhas menos a ultima
            // Ultima linha: custo
            ponteiro = file.getFilePointer();
            linha = file.readLine();
            valores = linha.split("\t");
            for(i = 0; i < provAfetados.length; i++)
            {
                custo = Double.parseDouble(valores[provAfetados[i] - 1]);
                valAntes[i] = custo;
                custo -= 1.0; // Diminui o custo em 1
                if(custo < 0.0) // Nao pode ser negativo
                    custo = 0.0;
                // Atualiza o custo
                valores[provAfetados[i] - 1] = "" + custo;
            }  
            // Volta o ponteiro pro inicio da linha
            file.seek(ponteiro);
            // Sobrescreve a ultima linha
            for(i = 0; i < valores.length; i++)            
                file.writeBytes(valores[i] + "\t"); 
                
            file.close();
            
            return valAntes;
        }
        catch (IOException ex)
        {
            System.err.println("Erro ao abrir arquivo " + nomeArq + ": " + ex.getMessage());
            return null;
        }
    }
    /**
     * 
     * @param numProv
     * @param numPIs
     * @param numDB
     * @param provAfetados
     * @param valAntigos 
     */
    private static void desfazDiminuiCusto(int numProv, int numPIs, int numDB, int[] provAfetados, double[] valAntigos)
    {
        if(provAfetados.length <= 0 || valAntigos.length <= 0 || provAfetados.length != valAntigos.length)
            throw new IllegalArgumentException("desfazDiminuiCusto: Numero dos provedores e valores são invalidos");
        
        int i;
        long ponteiro;              
        String linha, nomeArq = "Dados/Base" + numDB + "/DADOS" + numProv + "x" + numPIs + ".txt";
        String[] valores;
        try {   
            File arq = new File(nomeArq);
            RandomAccessFile file = new RandomAccessFile(arq, "rw");            
            // Primeira linha: nome dos provedores, ignora
            file.readLine();
            // Segunda linha: nome dos PIs, conta quantos PIs tem
            linha = file.readLine();            
            valores = linha.split(",");
            // Terceira linha: tipos dos PIs, ignora
            file.readLine();
            // Outras linhas: Valores, uma linha por PI
            for(i = 0; i < valores.length - 1; i++)
                file.readLine(); // Ignora todas as linhas menos a ultima
            // Ultima linha: custo
            ponteiro = file.getFilePointer();
            linha = file.readLine();
            valores = linha.split("\t");
            // Recupera o custo
            for(i = 0; i < provAfetados.length; i++)               
                valores[provAfetados[i] - 1] = "" + valAntigos[i];
              
            // Volta o ponteiro pro inicio da linha
            file.seek(ponteiro);
            // Sobrescreve a ultima linha
            for(i = 0; i < valores.length; i++)
                file.writeBytes(valores[i] + "\t"); 
            file.close();            
        }
        catch (IOException ex)
        {
            System.err.println("Erro ao abrir arquivo " + nomeArq + ": " + ex.getMessage());            
        }
    }
}
