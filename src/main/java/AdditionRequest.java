public class AdditionRequest {
    int arg1, arg2, result, reqNo;
    long timeStart;
    String senderId;

    public AdditionRequest(){};

    public AdditionRequest(int arg1, int arg2, int result, int reqNo, long timeStart, String senderId) {
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.result = result;
        this.reqNo = reqNo;
        this.timeStart = timeStart;
        this.senderId = senderId;
    }


    public int getArg1() {
        return arg1;
    }

    public void setArg1(int arg1) {
        this.arg1 = arg1;
    }

    public int getArg2() {
        return arg2;
    }

    public void setArg2(int arg2) {
        this.arg2 = arg2;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getReqNo() {
        return reqNo;
    }

    public void setReqNo(int reqNo) {
        this.reqNo = reqNo;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
