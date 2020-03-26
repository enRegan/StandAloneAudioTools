package com.regan.saata.bean;

import java.util.List;

public class QuestionInfoList extends BaseBean {
    private List<QuestionInfo> data;
//    {
//        "code": "0000",
//            "msg": "成功",
//            "count": 10,
//            "data": [
//        {
//            "questionId": "qweqeqdasdasd21313",
//                "userId": "21313asadeqweqwe31",
//                "state": "CREATE",
//                "createTime": "2020-01-31 12:39:00",
//                "updateTime": "2020-01-31 12:50:00",
//                "answer": [
//            {
//                "answer": "我再说一遍，我他妈日你妹，爱用不用",
//                    "answerTime": "2020-01-30 20:06:51"
//            },
//            {
//                "answer": "他妈再bb,我日你全家，我他妈日你妹，爱用不用",
//                    "answerTime": "2020-01-30 20:07:31"
//            }
//			],
//            "contact": "手机号：1378989789"
//        }
//	]
//    }

    public List<QuestionInfo> getData() {
        return data;
    }

    public void setData(List<QuestionInfo> data) {
        this.data = data;
    }
}
