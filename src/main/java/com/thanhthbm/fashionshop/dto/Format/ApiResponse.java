package com.thanhthbm.fashionshop.dto.Format;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.thanhthbm.fashionshop.constant.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
  private int statusCode;
  private ResponseStatus status;
  private String message;
  private T data;

  public static <T> ApiResponse<T> success(T data) {
    return ApiResponse.<T>builder()
        .statusCode(HttpStatus.OK.value())
        .status(ResponseStatus.SUCCESS)
        .message("Request processed successfully")
        .data(data)
        .build();
  }

  public static <T> ApiResponse<T> success(T data, String message) {
    return ApiResponse.<T>builder()
        .statusCode(HttpStatus.OK.value())
        .status(ResponseStatus.SUCCESS)
        .message(message)
        .data(data)
        .build();
  }

  public static <T> ApiResponse<T> created(T data) {
    return ApiResponse.<T>builder()
        .statusCode(HttpStatus.CREATED.value())
        .status(ResponseStatus.SUCCESS)
        .message("Resource created successfully")
        .data(data)
        .build();
  }

  public static <T> ApiResponse<T> fail(int statusCode, String message) {
    return ApiResponse.<T>builder()
        .statusCode(statusCode)
        .status(ResponseStatus.FAIL)
        .message(message)
        .build();
  }

  public static <T> ApiResponse<T> error(int statusCode, String message) {
    return ApiResponse.<T>builder()
        .statusCode(statusCode)
        .status(ResponseStatus.ERROR)
        .message(message)
        .build();
  }
}
