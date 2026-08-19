package com.eryck.SeaJavaVideoDownloader;

import java.io.PrintWriter;
import java.util.List;

public class ExecutorException extends Exception {
    private final List<String> command;
    private final int exitCode;
    private final String outputLog;
    private final Exception trace;

    ExecutorException(String message, List<String> command, int exitCode, String outputLog, Exception trace) {
        super(message);
        this.command = command;
        this.exitCode = exitCode;
        this.outputLog = outputLog;
        this.trace=trace;
    }

    public List<String> getCommand() {
        return command;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getOutputLog() {
        return outputLog;
    }

    public void printStackTrace(PrintWriter s) {
        trace.printStackTrace(s);
    }

    // Gera um relatório formatado e pronto para ser colado na TextArea
    public String getFormattedDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append(Strings.gIns().EXCEPTION_DETAILS);
        sb.append(Strings.gIns().EXIT_CODE).append(exitCode).append("\n\n");
        sb.append(Strings.gIns().COMMAND_EXECUTED).append(String.join(" ", command)).append("\n\n");
        sb.append(Strings.gIns().OUTPUT_LOG);
        sb.append(outputLog.isEmpty() ? Strings.gIns().NO_LOGS_RETURNED : outputLog);
        return sb.toString();
    }
}
