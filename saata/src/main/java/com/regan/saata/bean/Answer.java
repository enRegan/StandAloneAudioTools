package com.regan.saata.bean;

public class Answer extends BaseBean {
    //    "answer": [
//    {
//        "answer": "我再说一遍，我他妈日你妹，爱用不用",
//            "answerTime": "2020-01-30 20:06:51"
//    },
//    {
//        "answer": "他妈再bb,我日你全家，我他妈日你妹，爱用不用",
//            "answerTime": "2020-01-30 20:07:31"
//    }
//			],
    String answer;
    String answerTime;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(String answerTime) {
        this.answerTime = answerTime;
    }

    @Override
    public String toString() {
        return super.toString()
                + " answer : " + answer;
    }
}
