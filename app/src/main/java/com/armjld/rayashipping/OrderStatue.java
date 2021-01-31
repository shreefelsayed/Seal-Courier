package com.armjld.rayashipping;

public class OrderStatue {

    public String longStatue(String state) {
        String strState = "";
        switch (state) {
            case "placed" :
                strState = "لم يتم تحريك الشحنه بعد";
                break;
            case "accepted":
                strState = "تم اختيار الكابتن لنقل الشحنه";
                break;
            case "recived":
                strState = "تم استلام الشحنه من الكابتن";
                break;
            case "recived2":
                strState = "جاري توصيل الشحنه";
                break;
            case "readyD":
                strState = "جاري استلام الشحنه من الراسل";
                break;
            case "hubP":
            case "hubD":
                strState = "في المخزن";
                break;
            case "hub1Denied" :
            case "hub2Denied":
                strState = "مرتحع في المخزن";
                break;
            case "deniedD" :
            case "denied":
                strState = "تم عمل محاوله تسليم";
                break;
            case "delivered" :
                strState = "تم تسليم الشحنه للعميل";
                break;
            case "supDenied":
            case "capDenied" :
                strState = "جاري تسليم المرتجع للتاجر";
                break;
            case "deniedback":
                strState = "تم ارجاع الشحنه للتاجر";
                break;
            case "deleted":
                strState = "قام الراسل بحذف الشحنه";
                break;
            default:
                strState = "حاله الشحنه مجهوله .. تواصل مع خدمه العملاء";
                break;
        }

        return strState;
    }

    public String shortState(String state) {
        String strState = "";
        
        switch (state) {
            case "placed":
                strState = "يتم مراجعه الشحنه";
                break;
            case "accepted":
                strState = "قيد الاستلام";
                break;
            case "recived":
                strState = "في انتظار تأكيد الاستلام";
                break;
            case "recived2" :
                strState = "جاري تسليم الشحنه للمخزن";
                break;
            case "hubP" :
            case "hubD" :
                strState = "في المخزن";
                break;
            case "supD" :
            case "readyD" :
                strState = "قيد التسليم";
                break;
            case "delivered" :
                strState = "تم التسليم و التحصيل";
                break;
            case "denied" :
                strState = "مرتجع";
                break;
            case "deniedD" :
                strState = "في مخزن المرتجعات";
                break;
            case "hub1Denied" :
            case "supDenied" :
            case "capDenied" :
            case "hub2Denied" :
                strState = "جاري استرجاع الشحنه";
                break;
            case "deniedback" :
                strState = "تم استلام المرتجع";
                break;
            case "deleted" :
                strState = "تم الغاء الشحنه";
                break;
            default:
                strState = "";
                break;
        }

        return strState;
    }
}
