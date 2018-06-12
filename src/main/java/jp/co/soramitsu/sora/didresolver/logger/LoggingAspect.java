package jp.co.soramitsu.sora.didresolver.logger;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

  @Around("execution(* jp.co.soramitsu.sora.*.*.*.*(..))")
  public Object log(ProceedingJoinPoint point) throws Throwable {
    if (log.isDebugEnabled()) {
      log.debug("\n" +
              "\n>>>>>>>>>>>>>>>>>>>>" +
              "\nENTER TO METHOD:" +
              "\n{}.{}() " +
              "\nPASSED ARGUMENTS:" +
              "\n{}" +
              "\n>>>>>>>>>>>>>>>>>>>>" +
              "\n",
          point.getSignature().getDeclaringTypeName(),
          point.getSignature().getName(),
          Arrays.toString(point.getArgs())
      );
    }
    try {
      Object result = point.proceed();
      if (log.isDebugEnabled()) {
        log.debug("\n" +
                "\n<<<<<<<<<<<<<<<<<<<<" +
                "\nEXIT FROM METHOD:" +
                "\n{}.{}() " +
                "\nEXECUTION RESULT:" +
                "\n{}" +
                "\n<<<<<<<<<<<<<<<<<<<<" +
                "\n",
            point.getSignature().getDeclaringTypeName(),
            point.getSignature().getName(), result);
      }
      return result;
    } catch (Exception e) {
      log.error("\n" +
              "\n<<<<<<<<<<<<<<<<<<<<" +
              "\nEXCEPTION: " +
              "\n{} " +
              "\nIN METHOD:" +
              "\n{}.{}()" +
              "\n<<<<<<<<<<<<<<<<<<<<" +
              "\n",
          e.toString(),
          point.getSignature().getDeclaringTypeName(),
          point.getSignature().getName());
      throw e;
    }
  }
}
