package jp.co.soramitsu.sora.didresolver.config;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.security.MessageDigest.getInstance;
import static org.apache.commons.codec.binary.Hex.encodeHexString;
import static org.slf4j.MDC.put;
import static org.slf4j.MDC.remove;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.owasp.security.logging.mdc.MDCFilter;
import org.springframework.context.annotation.Configuration;

@Configuration
@WebFilter(filterName = "mdcFilter", urlPatterns = "/*")
@Slf4j
public class LoggingFilter extends MDCFilter {

  private static final String MD_5 = "MD5";
  private static final String REQUEST = "request";
  private static final String REQUEST_SESSION = "requestSession";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    val req = (HttpServletRequest) request;
    val serverHost = req.getServerName();
    val serverPort = req.getServerPort();
    val clientHost = req.getRemoteHost();
    val clientIp = req.getRemoteAddr();
    val contentType = req.getContentType();
    val encoding = req.getCharacterEncoding();
    val contentLength = req.getContentLength();
    if (req.getSession() != null) {
      MessageDigest md;
      try {
        md = getInstance(MD_5);
      } catch (NoSuchAlgorithmException e) {
        throw new IllegalStateException(e);
      }
      val hashedSession = encodeHexString(md.digest(req.getSession().getId().getBytes(UTF_8)));
      put(REQUEST, hashedSession);
    }
    log.info("Incoming request meta: Client Host={}; "
            + "Client IP={}; Content Type={}; Encoding={}; Server Host={}; Server Port={}; Content Length={}; Method={}; URL={}; Query params={}",
        clientHost, clientIp, contentType, encoding, serverHost, serverPort, contentLength,
        req.getMethod(), req.getRequestURI(), req.getQueryString()
    );
    chain.doFilter(request, response);

    remove(REQUEST_SESSION);
  }

}
