package com.regan.saata.bean;

public class QuestionInfo extends BaseBean {
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
    String questionId;
    String userId;
    String state;
    String createTime;
    String updateTime;
    //    List<Answer> answer;
//    Answer answer;
    String answer;
    //    List<String> answer;
    String loginNo;
    String packageName;
    String channelNo;
    String platform;
    String outterVersion;
    String innerVersion;
    String contact;
    String question;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

//    public List<Answer> getAnswer() {
//        return answer;
//    }
//
//    public void setAnswer(List<Answer> answer) {
//        this.answer = answer;
//    }


//    public Answer getAnswer() {
//        return answer;
//    }
//
//    public void setAnswer(Answer answer) {
//        answer = answer;
//    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }


//    public List<String> getAnswer() {
//        return answer;
//    }
//
//    public void setAnswer(List<String> answer) {
//        this.answer = answer;
//    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getLoginNo() {
        return loginNo;
    }

    public void setLoginNo(String loginNo) {
        this.loginNo = loginNo;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getOutterVersion() {
        return outterVersion;
    }

    public void setOutterVersion(String outterVersion) {
        this.outterVersion = outterVersion;
    }

    public String getInnerVersion() {
        return innerVersion;
    }

    public void setInnerVersion(String innerVersion) {
        this.innerVersion = innerVersion;
    }

    @Override
    public String toString() {
        return super.toString() + " questionId : " + questionId
                + " \nuserId : " + userId
                + " \nstate : " + state
                + " \ncreateTime : " + createTime
                + " \nupdateTime : " + updateTime
                + " \nloginNo : " + loginNo
                + " \npackageName : " + packageName
                + " \nchannelNo : " + channelNo
                + " \nplatform : " + platform
                + " \noutterVersion : " + outterVersion
                + " \ninnerVersion : " + innerVersion
                + " \nquestion : " + question
                + " \nanswer : " + answer
                + " \ncontact : " + contact;
    }
}
