package cn.tedu.csmall.passport.ex.handler;

import cn.tedu.csmall.passport.ex.ServiceException;
import cn.tedu.csmall.passport.web.JsonResult;
import cn.tedu.csmall.passport.web.ServiceCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public JsonResult handleServiceException(ServiceException e) {
        log.warn("程序运行过程中出现了 ServiceException，将统一处理！");
        log.warn("异常信息：{}", e.getMessage());
        return JsonResult.fail(e.getServiceCode(), e.getMessage());
    }

    @ExceptionHandler
    public JsonResult handleBindException(BindException e) {
        log.warn("程序运行过程中出现了 BindException，将统一处理！");
        log.warn("异常信息：{}", e.getMessage());
        // 【解决方案-1】使用1个字符串表示1个错误信息
        String message = e.getFieldError().getDefaultMessage();
        return JsonResult.fail(ServiceCode.ERR_BAD_REQUEST, message);

        // 【解决方案-2】使用1个字符串表示错误信息
        // StringJoiner stringJoiner = new StringJoiner("，", "请求参数错误，", "！");
        // List<FieldError> fieldErrors = e.getFieldErrors();
        // for (FieldError fieldError : fieldErrors) {
        //    String defaultMessage = fieldError.getDefaultMessage();
        //    stringJoiner.add(defaultMessage);
        // }
        // return stringJoiner.toString();

        // 【解决方案-3】使用集合表示多个错误信息，需要将当前方法的返回值类型声明为对应的集合类型
        // List<String> messageList = new ArrayList<>();
        // List<FieldError> fieldErrors = e.getFieldErrors();
        // for (FieldError fieldError : fieldErrors) {
        //    String defaultMessage = fieldError.getDefaultMessage();
        //    messageList.add(defaultMessage);
        // }
        // return messageList;
    }

    @ExceptionHandler
    public JsonResult handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("程序运行过程中出现了 ConstraintViolationException，将统一处理！");
        log.warn("异常信息：{}", e.getMessage());
        String message = null;
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            message = constraintViolation.getMessage();
        }

        //JsonResult jsonResult = new JsonResult();
        //jsonResult.setState(3);
        //jsonResult.setMessage(message);
        return JsonResult.fail(ServiceCode.ERR_BAD_REQUEST, message);
    }

    @ExceptionHandler({
            InternalAuthenticationServiceException.class,
            BadCredentialsException.class
    })
    public JsonResult handleAuthenticationException(AuthenticationException e) {
        // 【注意】在项目正式上线时，禁止使用 e.printStackTrace();
        log.warn("程序运行过程中出现了 AuthenticationServiceException，将统一处理！");
        log.warn("异常：", e); // 取代 e.printStackTrace();，效果相同，注意，第1个参数中不要使用 {} 进行占位
        String message = "登陆失败，用户名或密码错误！";

        //JsonResult jsonResult = new JsonResult();
        //jsonResult.setState(99999);
        //jsonResult.setMessage(message);
        return JsonResult.fail(ServiceCode.ERR_UNAUTHORIZED, message);
    }

    @ExceptionHandler
    public JsonResult handleDisabledException(DisabledException e) {
        // 【注意】在项目正式上线时，禁止使用 e.printStackTrace();
        log.warn("程序运行过程中出现了 DisabledException，将统一处理！");
        log.warn("异常：", e); // 取代 e.printStackTrace();，效果相同，注意，第1个参数中不要使用 {} 进行占位
        String message = "登陆失败，账号已经被禁用！";

        //JsonResult jsonResult = new JsonResult();
        //jsonResult.setState(99999);
        //jsonResult.setMessage(message);
        return JsonResult.fail(ServiceCode.ERR_UNKNOWN, message);
    }

    @ExceptionHandler
    public JsonResult handleAccessDeniedException(AccessDeniedException e) {
        // 【注意】在项目正式上线时，禁止使用 e.printStackTrace();
        log.warn("程序运行过程中出现了 AccessDeniedException，将统一处理！");
        log.warn("异常：", e); // 取代 e.printStackTrace();，效果相同，注意，第1个参数中不要使用 {} 进行占位
        String message = "您当前登陆的账号无此权限，禁止访问！";
        return JsonResult.fail(ServiceCode.ERR_FORBIDDEN, message);
    }

    @ExceptionHandler
    public JsonResult handleThrowable(Throwable e) {
        // 【注意】在项目正式上线时，禁止使用 e.printStackTrace();
        log.warn("程序运行过程中出现了 Throwable，将统一处理！");
        log.warn("异常：", e); // 取代 e.printStackTrace();，效果相同，注意，第1个参数中不要使用 {} 进行占位
        String message = "服务器忙，请稍后再试！【在开发过程中，如果看到此提示，应该检查服务器端的控制台，分析异常，并在全局异常处理器中补充处理对应异常的方法】";

        //JsonResult jsonResult = new JsonResult();
        //jsonResult.setState(99999);
        //jsonResult.setMessage(message);
        return JsonResult.fail(ServiceCode.ERR_UNKNOWN, message);
    }

}