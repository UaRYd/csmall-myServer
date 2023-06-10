# 关于响应结果类型

在项目中，已经开始使用自定义的`JsonResult`类作为响应结果类型：

```java
@Data
public class JsonResult implements Serializable {

    private Integer state;
    private String message;

}
```

然后，在处理请求或处理异常时，都将响应`JsonResult`类型的结果，例如：

```java
@PostMapping("/add-new")
public JsonResult addNew(@Valid AlbumAddNewParam albumAddNewParam) {
    log.debug("开始处理【添加相册】的请求，参数：{}", albumAddNewParam);
    albumService.addNew(albumAddNewParam);

    JsonResult jsonResult = new JsonResult(); // <<< 本次关注的代码
    jsonResult.setState(1);                   // <<< 本次关注的代码
    jsonResult.setMessage("添加成功！");        // <<< 本次关注的代码
    return jsonResult;                        // <<< 本次关注的代码
}
```

以上使用了4行代码来创建`JsonResult`对象、为属性赋值并返回，是不合适的，应该简化这些代码！

**解决方案-1：构造方法**

在`JsonResult`类中添加带参数的构造方法：

```java
@Data
public class JsonResult implements Serializable {

    private Integer state;
    private String message;

    public JsonResult(Integer state, String message) {
        this.state = state;
        this.message = message;
    }
    
}
```

则控制器的代码可以调整为：

```java
@PostMapping("/add-new")
public JsonResult addNew(@Valid AlbumAddNewParam albumAddNewParam) {
    log.debug("开始处理【添加相册】的请求，参数：{}", albumAddNewParam);
    albumService.addNew(albumAddNewParam);

    return new JsonResult(1, "添加成功！"); // <<< 新的代码
}
```

以上做法可以成功的精简构建`JsonResult`对象的代码，但是，存在问题：

- 传入的参数的可读性可能较差，当构造方法有多个参数时，可能不便于了解传入的各值是哪些属性的值
- 构造方法的名称必须与类名完全相同，则不便于表达某些含义

**解决方案-2：链式方法**

将`JsonResult`类中的Setter方法全部调整为返回当前对象的方法：

```java
// @Data
public class JsonResult implements Serializable {

    private Integer state;
    private String message;

    public JsonResult setState(Integer state) {
        this.state = state;
        return this;
    }
    
    public JsonResult setMessage(String message) {
        this.message = message;
        return this;
    }
    
    // 其它方法
    
}
```

则控制器中的代码可以调整为：

```java
@PostMapping("/add-new")
public JsonResult addNew(@Valid AlbumAddNewParam albumAddNewParam) {
    log.debug("开始处理【添加相册】的请求，参数：{}", albumAddNewParam);
    albumService.addNew(albumAddNewParam);

    // return new JsonResult(1, "添加成功");
    return new JsonResult().setState(1).setMessage("添加成功");
}
```

如果你的项目是基于Lombok的，其实，你并不需要自行调整类中的Setter方法，只需要在类上添加`@Accessors(chain = true)`注解配置，则Lombok生成的Setter方法也都是返回当前对象的，所以，也可以使用链式写法！

```java
@Data
@Accessors(chain = true)
public class JsonResult implements Serializable {

    private Integer state;
    private String message;
    
}
```

以上做法可以成功的解决构造方法多参数时各参数值意义不明确的问题，但是，这种做法也存在问题：

- 当属性较多时，需要调用多个Setter方法，则代码篇幅可能较长
  - 无解
  - 不一定真的的算是“问题”

**解决方法-3：静态方法**

在`JsonResult`类中添加新的方法：

```java
public static JsonResult ok() {
    JsonResult jsonResult = new JsonResult();
    jsonResult.setState(1);
    return jsonResult;
}
```

则处理请求的方法可以调整为：

```java
@PostMapping("/add-new")
public JsonResult addNew(@Valid AlbumAddNewParam albumAddNewParam) {
    // ...
    return JsonResult.ok();
}
```

这种做法的优点在于：方法名称是自定义的，可以使用更加贴切的方法名，以表示设计意图！

**使用枚举**

由于“操作失败”的原因可能有多种（例如相册名称已经被占用、提交的请求参数基本格式有误等），所以，表示失败的方法需要参数，使得“操作失败”时可以传入不同的值，最终可以向客户端响应不同的结果，例如：

```java
public static JsonResult fail(Integer state, String message) {
    JsonResult jsonResult = new JsonResult();
    jsonResult.setState(state);
    jsonResult.setMessage(message);
    return jsonResult;
}
```

则处理异常时可以是：

```java
@ExceptionHandler
public JsonResult handleServiceException(ServiceException e) {
    return JsonResult.fail(2, e.getMessage());
}
```

为了保证传入的参数是有效的，避免随意传值（毕竟以上`fail`方法的第1个参数是`Integer`类型的，有40多亿种可能的值），可以使用枚举进行限制！

首先，定义枚举类型：

```java
public enum ServiceCode {

    OK(20000),
    ERR_BAD_REQUEST(40000),
    ERR_CONFLICT(40900),
    ERR_UNKNOWN(99999)
    ;

    private Integer value;

    public Integer getValue() {
        return value;
    }

    ServiceCode(Integer value) {
        this.value = value;
    }

}
```

然后，将`fail()`方法的第1个参数类型改为枚举，并在方法体中获取枚举参数值对应的数值：

```java
public static JsonResult fail(ServiceCode serviceCode, String message) {
    JsonResult jsonResult = new JsonResult();
    jsonResult.setState(serviceCode.getValue());
    jsonResult.setMessage(message);
    return jsonResult;
}
```

则在处理异常时需要调整为：

```java
@ExceptionHandler
public JsonResult handleServiceException(ServiceException e) {
    return JsonResult.fail(ServiceCode.ERR_CONFLICT, e.getMessage());
}
```

















```java
new Category()
    .setEnable(1)
    .setIsDisplay(1)
    .setSort(0);
```



```javascript
if (response.data.state == 1) {
    // 成功
}
```





枚举



#000000

#FF0000

#0000FF

#00FF00

#FFFFFF

16 x 16 x 16 x 16 x 16 x 16



