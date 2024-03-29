///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS info.picocli:picocli:4.7.5
//DEPS org.apache.pdfbox:pdfbox:2.0.22

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "MergePdf", //
        mixinStandardHelpOptions = true, //
        version = "MergePdf 0.2", //
        description = "MergePdf made with jbang", //
        helpCommand = true)
class MergePdf implements Callable<Integer> {

    private static final String OUTPUT_DEF_VAL = "merged";
    private static final String PDF_EXT = ".pdf";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd-HH_mm");

    @Parameters(index = "0", //
            description = "Arquivos individuais de entrada", //
            arity = "0..*")
    private File[] files = new File[]{};

    @Option(names = { "-d", "--dir" }, //
            description = "Diretórios para serem lidos", //
            arity = "0..*")
    private File[] dirs = new File[]{};

    @Option(names = { "-o", "--output" }, //
            description = "Nome do arquivo de saída", //
            defaultValue = OUTPUT_DEF_VAL)
    private String outputFileName;

    public static void main(String... args) {
        int exitCode = new CommandLine(new MergePdf()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        if (emptyParams() && emptyArgs()) {
            printErrorMsgAnsi("Nenhum arquivo para merge foi informado");
            return 1;
        }

        PDFMergerUtility pmu = new PDFMergerUtility();
        String out = getRealOutputFileName();
        pmu.setDestinationFileName(out);
        pmu.setDocumentMergeMode(PDFMergerUtility.DocumentMergeMode.OPTIMIZE_RESOURCES_MODE);

        var listDirFiles = Arrays.stream(dirs)
                        .map(dir -> dir.listFiles())
                        .flatMap(files -> Arrays.stream(files))
                        .collect(Collectors.toList());
                        
        Stream.concat(Arrays.stream(files), listDirFiles.stream())
                .filter(file -> file.getName().endsWith(PDF_EXT))
                .filter(file -> file.exists() && file.canRead())
                .forEach(file -> {
                    try {
                        pmu.addSource(file);
                        printInfoMsgAnsi("PDF adicionado: " + file.getName());
                    } catch (IOException e) {
                        printErrorMsgAnsi("FALHA ao ler arquivo " + file.getName() + ". Erro: " + e.getMessage());
                    }
                });

        try {
            pmu.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
            printSuccessMsgAnsi("Arquivo gerado " + out);
            return 0;
        } catch (IOException e) {
            printErrorMsgAnsi("FALHA ao gerar arquivo... " + e.getMessage());
            return 1;
        }
    }

    private void printlnAnsi(String msg) {
        System.out.println(CommandLine.Help.Ansi.AUTO.string(msg));
    }

    private void printErrorMsgAnsi(String msg) {
        printlnAnsi("@|red " + msg + "|@");
    }

    private void printWarningMsgAnsi(String msg) {
        printlnAnsi("@|yellow " + msg + "|@");
    }

    private void printSuccessMsgAnsi(String msg) {
        printlnAnsi("@|green " + msg + "|@");
    }

    private void printInfoMsgAnsi(String msg) {
        printlnAnsi("@|blue " + msg + "|@");
    }

    private boolean emptyParams() {
        return files == null || files.length == 0;
    }

    private boolean emptyArgs() {
        return dirs == null || dirs.length == 0;
    }

    /**
     * Adiciona um timestamp ao final do nome do arquivo resultante caso nenhum nome
     * customizado tenha sido passado como parametro. Adiciona a extensão do
     * arquivo, caso a mesma não tenha sido passada no parametro customizado.
     */
    private String getRealOutputFileName() {
        if (OUTPUT_DEF_VAL.equals(outputFileName)) {
            String out = OUTPUT_DEF_VAL + "_" + sdf.format(Timestamp.from(Instant.now())) + PDF_EXT;
            printWarningMsgAnsi("Usando valor default gerado para nome de arquivo " + out);
            return out;
        }
        return outputFileName.endsWith(PDF_EXT) //
                ? outputFileName
                : outputFileName + PDF_EXT;
    }

}
