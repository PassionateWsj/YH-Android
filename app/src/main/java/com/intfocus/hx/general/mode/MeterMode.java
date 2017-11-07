package com.intfocus.hx.general.mode;

import com.zbl.lib.baseframe.core.AbstractMode;

/**
 * 仪表盘-数据处理模块
 * Created by zbaoliang on 17-4-28.
 */
public class MeterMode extends AbstractMode {
    @Override
    public void requestData() {

    }
//
//    Context ctx;
//    String urlString;
//    String messageUrlString;
//    JSONObject user;
//    SharedPreferences mUserSP;
//
//    public MeterMode(Context ctx) {
//        this.ctx = ctx;
//    }
//
//    public String getKpiUrl() {
//        mUserSP = ctx.getSharedPreferences("UserBean", MODE_PRIVATE);
//        String url = String.format(K.K_KPI_MOBILE_DATA_PATH, K.kBaseUrl, mUserSP.getString(K_CURRENT_UI_VERSION, "v2"),
//                String.valueOf(mUserSP.getString(kGroupId, "0")), String.valueOf(mUserSP.getString(kRoleId, "0")));
//        return url;
//    }
//
//    public String getMessageUrl() {
//        String url = String.format(K.K_MESSAGE_DATA_MOBILE_PATH, K.kBaseUrl, UserBean.INSTANCE.getUser_role_id(),
//                UserBean.INSTANCE.getUser_group_id(),
//                UserBean.INSTANCE.getUser_id());
//        return url;
//    }
//
//    @Override
//    public void requestData() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                urlString = getKpiUrl();
//                if (!urlString.isEmpty()) {
//                    Log.i("kpiUrl", urlString);
//                    Map<String, String> response = HttpUtil.httpGet(ctx, urlString, new HashMap<String, String>());
//                    String result = response.get("body");
//                    if (StringUtil.isEmpty(result)) {
//                        MeterRequestResult result1 = new MeterRequestResult(true, 400);
//                        EventBus.getDefault().post(result1);
//                        return;
//                    }
//                    analysisData(result);
//                } else {
//                    MeterRequestResult result1 = new MeterRequestResult(false, 400);
//                    EventBus.getDefault().post(result1);
//                    return;
//                }
//            }
//        }).start();
//    }
//
//    /**
//     * 获取公告栏消息
//     *
//     * @return message
//     */
//    public String[] getMessage() {
//        String[] message = null;
//        messageUrlString = getMessageUrl();
//        if (messageUrlString.isEmpty()) {
//            return null;
//        }
//
//        Gson gson = new Gson();
//        Map<String, String> response = HttpUtil.httpGet(ctx, messageUrlString, new HashMap<String, String>());
//        JsonObject returnData = new JsonParser().parse(response.get("body")).getAsJsonObject();
//        Message mMessage = gson.fromJson(returnData, Message.class);
//
//        String messageNumber = String.format("消息(%d): ", mMessage.data.size());
//        String firstMessage = messageNumber + mMessage.data.get(0).getTitle();
//        String secondMessage = messageNumber + mMessage.data.get(1).getTitle();
//
//        message = new String[]{firstMessage, secondMessage};
//        return message;
//    }
//
//    /**
//     * 解析数据
//     *
//     * @param result
//     */
//    private MeterRequestResult analysisData(String result) {
//        try {
//            JSONObject jsonObject = new JSONObject(result);
//            if (jsonObject.has("code")) {
//                int code = jsonObject.getInt("code");
//                if (code != 200) {
//                    MeterRequestResult result1 = new MeterRequestResult(true, code);
//                    EventBus.getDefault().post(result1);
//                    return result1;
//                }
//            }
//
//            if (jsonObject.has("data")) {
//                String data = jsonObject.getString("data");
//                data = data.replace("\"arrow\":null", "\"arrow\":-1");
//                data = data.replace("\"number\":null", "\"number\":0.123456789");
//                data = data.replace("null", "0");
//                ArrayList<MererEntity> datas = (ArrayList<MererEntity>) JSON.parseArray(data, MererEntity.class);
//                ArrayList<MererEntity> topData = new ArrayList<>();
//                Iterator<MererEntity> iterator = datas.iterator();
//                while (iterator.hasNext()) {
//                    MererEntity entity = iterator.next();
//                    if (entity.is_stick()) {
//                        topData.add(entity);
//                        iterator.remove();
//                    }
//                }
//                MeterRequestResult result1 = new MeterRequestResult(true, 200);
//                result1.setTopDatas(topData);
//                result1.setBodyDatas(datas);
//                EventBus.getDefault().post(result1);
//                return result1;
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//            MeterRequestResult result1 = new MeterRequestResult(false, -1);
//            EventBus.getDefault().post(result1);
//        }
//        MeterRequestResult result1 = new MeterRequestResult(false, 0);
//        EventBus.getDefault().post(result1);
//        return result1;
//    }
}
