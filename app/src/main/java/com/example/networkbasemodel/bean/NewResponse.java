package com.example.networkbasemodel.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewResponse extends BaseResponse {
    @SerializedName("result")
    private ResBean result;

    public static class ResBean {
        private String stat;
        private List<NewBean> data;
        private String page;
        private String pageSize;

        public String getStat() {
            return stat;
        }

        public void setStat(String stat) {
            this.stat = stat;
        }

        public List<NewBean> getData() {
            return data;
        }

        public void setData(List<NewBean> data) {
            this.data = data;
        }

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }

        public String getPageSize() {
            return pageSize;
        }

        public void setPageSize(String pageSize) {
            this.pageSize = pageSize;
        }
    }

    public ResBean getResult() {
        return result;
    }

    public void setResult(ResBean result) {
        this.result = result;
    }
}
