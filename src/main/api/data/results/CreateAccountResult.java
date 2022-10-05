package data.results;

import data.Account;
import data.Proxy;

import java.util.List;

public class CreateAccountResult {
    boolean succesfull;
    Exception exception;
    List<LogMessage> logMessages;
    Account account;
    IpInfo ipInfo;
    Proxy proxy;

    public CreateAccountResult(boolean succesfull, Exception exception, List<LogMessage> logMessages, Account account, IpInfo ipInfo, Proxy proxy) {
        this.succesfull = succesfull;
        this.exception = exception;
        this.logMessages = logMessages;
        this.account = account;
        this.ipInfo = ipInfo;
        this.proxy = proxy;
    }
}
