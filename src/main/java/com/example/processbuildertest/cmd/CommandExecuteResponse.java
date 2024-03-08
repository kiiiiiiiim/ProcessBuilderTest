package com.example.processbuildertest.cmd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CommandExecuteResponse {
    private boolean success;
    private String  errorCode;
    private String  data;
}
