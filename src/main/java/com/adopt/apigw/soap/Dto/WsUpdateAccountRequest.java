package com.adopt.apigw.soap.Dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class WsUpdateAccountRequest {
        protected String actionItem;
        protected String requestId;
        protected String userName;
        protected String password;
        protected List<WsUpdateAccountRequest.Item> item;

        public static class Item {
            private String key;
            private String value;

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }
