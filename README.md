# androidNetWorkBaseModel
# android Rxjava+retrofit+okhttp 分装的网络基础组件

1.项目概述
此项目是使用Rxjava + retrofit + okhttp分装的一个网络请求基础组件；
业务层直接引用使用；

本代码业务层是----》网络请求一个聚合数据平台提供的top新闻接口，将数据展示出来

AGP 构建版本是"8.13.0-alpha03"，比较新各位Androidstudio太老可能拉下来，要改构建环境

AGP和studio 版本兼容可在这个网站看：(https://blog.csdn.net/ys743276112/article/details/141501346)

2. 业务层使用
   //添加网络模块依赖
    implementation project(':netWorkModel')

   Application里面初始化，网络模块
   
    mNetworkRequiredInfo = new NetworkRequiredInfo.Builder(this)
                .baseUrl(JUHE_BASE_URL)
                .build();
        NetworkApi.init(mNetworkRequiredInfo);
    mNetworkRequiredInfo是用建造者模式创建的一个用来保存网络请求的公共参数，就不用重复写了，提高效率
    这边是通过baseUrl创建的，比如后面要访问其他平台接口，就可以扩展；
   
   业务层接口调用
    public static void getNewsWithNetWork(String type, int page, int pageSize, int isFilter, BaseObserver<NewResponse> callBack) {
        HashMap<String, String> parms = new HashMap<>();
        parms.put("type", type);
        parms.put("page", String.valueOf(page));
        parms.put("page_size", String.valueOf(pageSize));
        parms.put("is_filter", String.valueOf(isFilter));
        parms.putAll(COM_PARMA);
   //这边就是调用的网络组建模块，接口见ApiService.class
        NetworkApi.createService(ApiService.class)
                .getNews(parms)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callBack);

        //下面这种是通过用compose操作符将链式调用里面重复的线程调度代码封装成一个新的Observable
        //核心作用是通过封装多个操作符实现逻辑复用，同时保持链式调用的简洁性
//        NetworkApi.createService(ApiService.class)
//                .getNews(parms)
//                .compose(NetworkApi.<NewResponse>applySchedulers())
//                .subscribe(callBack);
    }
    下拉刷新，上拉加载更多
     mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPage = 1;
                mData.clear();
                getData();
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPage++;
                getData();
            }
        });

          private void getData() {
        NetWorkBus.getNewsWithNetWork("top", mPage, mPageSize, mIsFilter, new BaseObserver<NewResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(NewResponse newResponse) {
                mData.addAll(newResponse.getResult().getData());
                mNewAdapter.notifyDataSetChanged();
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore();
            }

            @Override
            public void onFailure(Throwable e) {
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore();
            }
        });
    }
使用很简单吧！

3. 网络组件说明
        /**
     * 配置OkHttp
     *
     * @return OkHttpClient
     */
    private static OkHttpClient getOkHttpClient() {
        if (sOkHttpClient == null) {
            //OkHttp构建器
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            //设置缓存大小
            int cacheSize = 100 * 1024 * 1024;
            //设置OkHttp网络缓存
            builder.cache(new Cache(sINetworkRequiredInfo.getApplication().getCacheDir(), cacheSize));
            //设置网络请求超时时长，这里设置为6s
            builder.connectTimeout(6, TimeUnit.SECONDS);
            //添加请求拦截器，如果接口有请求头的话，可以放在这个拦截器里面
            builder.addInterceptor(new RequestInterceptor(sINetworkRequiredInfo));
            //添加返回拦截器，可用于查看接口的请求耗时，对于网络优化有帮助
            builder.addInterceptor(new ResponseInterceptor());
            //当程序在debug过程中则打印数据日志，方便调试用。
            if (sINetworkRequiredInfo != null && sINetworkRequiredInfo.isDebug()) {
                //sINetworkRequiredInfo不为空且处于debug状态下则初始化日志拦截器
                HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
                //设置要打印日志的内容等级，BODY为主要内容，还有BASIC、HEADERS、NONE。
                System.out.println("接口请求日志开始------------");
                //httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                //将拦截器添加到OkHttp构建器中
                builder.addInterceptor(httpLoggingInterceptor);
            }
            //OkHttp配置完成
            sOkHttpClient = builder.build();
        }
        return sOkHttpClient;
    }

    /**
     * 配置Retrofit
     *
     * @param serviceClass 服务类
     * @return Retrofit
     */
    private static Retrofit getRetrofit(Class serviceClass) {
        if (sRetrofitHashMap.get(sINetworkRequiredInfo.getBaseUrl() + serviceClass.getName()) != null) {
            //刚才上面定义的Map中键是String，值是Retrofit，当键不为空时，必然有值，有值则直接返回。
            return sRetrofitHashMap.get(sINetworkRequiredInfo.getBaseUrl() + serviceClass.getName());
        }
        //初始化Retrofit  Retrofit是对OKHttp的封装，通常是对网络请求做处理，也可以处理返回数据。
        //Retrofit构建器
        Retrofit.Builder builder = new Retrofit.Builder();
        //设置访问地址
        builder.baseUrl(sINetworkRequiredInfo.getBaseUrl());
        //设置OkHttp代理
        builder.client(getOkHttpClient());
        //设置数据解析器
        builder.addConverterFactory(GsonConverterFactory.create());
        //设置请求回调，使用RxJava 对网络返回进行处理
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        //retrofit配置完成
        Retrofit retrofit = builder.build();
        //放入Map中
        sRetrofitHashMap.put(sINetworkRequiredInfo.getBaseUrl() + serviceClass.getName(), retrofit);
        //最后返回即可
        return retrofit;
    }



