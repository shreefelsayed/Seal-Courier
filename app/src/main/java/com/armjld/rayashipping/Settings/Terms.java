package com.armjld.rayashipping.Settings;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import com.armjld.rayashipping.R;


public class Terms extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        finish();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        TextView txtTerms = findViewById(R.id.txtTerms);
        ImageView btnBack = findViewById(R.id.btnBack);
        txtTerms.setMovementMethod(new ScrollingMovementMethod());
        btnBack.setOnClickListener(v -> finish());
        String privcyCode = "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 13px; line-height: normal; font-family: \"Times New Roman\";'><strong>الشروط و الاحكام :</strong></p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'><strong>البوليصة :</strong></p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'>&nbsp;بالتوقيع ز من فضلك لاحظ انه لا يحق للتاجر الحصول على تعويض عن الشحنة المفقودة / التالفة إذا لم يتمكن الراسل من تقديم البوليصة.</p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'><strong>إخلاء المسؤولية عن المواد الهشة :</strong></p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'><strong>انفيو</strong> و شركاؤها في الشحن غير مسؤولين عن اختيار الراسل لشحن العناصر الهشة / سهلة الكسر . اسأل مديري حسابك عن اقترحات التعبة و التغليف إذا كان منتجك هشاً .</p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'><strong>المواد المحظورة:</strong></p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'>النقد &ndash; الأموال المستردة مقابل البضائع القديمة &ndash; الذهب &ndash; البضائع المسروقة &ndash; التبغ &ndash; أي سوائل / كحولية &ndash; أغذية انتهت صلاحيتها أو تحتاج إلى ظروف تخزين خاصة &ndash; حيوانات اليفة &ndash; أسلحة &ndash; أي عنصر تزيد قيمته عن 10000 جنية.</p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'><strong>سياسة الإرجاع و التبديل:&nbsp;</strong></p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'>- إذا تم إلغاء التسليم أثناء قيام المندوب بتسليم الشحنة . فسيتم تحصيل مبلغ الشحن كامل .</p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'>- إذا لم يكن عميلك متاحاً في عموان التسليم . فسنجري تجربة اخرى للتسليم . إذا فشلنا في الوصول إليه . فسيتم إرجاع الطلب مباشرة الى مقر إستلام الشحنة . و سيتم تحصيل رسوم الشحن منك بالكامل.</p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'>- في حال رفض العميل استلام الشحنة و رفض العميل دفع رسوم الشحن ؛ سيتم خصم القيمة بالكامل من حسابك.</p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'>اخرى:</p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'>1- الراسل مسؤول عن عملية العبئة ؛ يمكن ان تدعمك <strong>envio</strong> بالنصائح و الحيل و سنوفر لك مواد التعبئة و التغليف مجاناً.</p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'>2- يجب على الراسل ملء البوليصة . مع إظهار موقع التسليم &nbsp;الدقيق . يحق للمندوب عدم تلقي أي طلبات في حالة قيامك بتعبئة البوليصة لكل طلب بشكل كامل وواضح.</p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'>3- لا يمكن تاجيل تسليم الشحنة لاكثر من 7 ايام . و إلا فسيتم إرجاع الشحنة إلى الراسل و سيتم خصم وسوم الشحن من حساب الراسل.</p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'>4- لا تقدم سياسة <strong>envio</strong> لعملائك فتح الشحنة . لذلك تحتاج إلى التاكد من عميلك أنه / أنها بحاجة إلى طلب المنتج قبل الشحن.</p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'>5- يحق <strong>لenvio</strong> مراجعو و تغيير أي من الشروط و الأحكلم . و سيتك إخطارك على الفور.</p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'>6- يحق <strong>لenvio</strong> زيادة رسوم الشحن في حالة وجود أي زيادة في رسوم الوقود . سيتم إعلامك بذلك . يمكن زيادة رسوم الشحن سنوياً ؛ سيتم إخطارك قبل شهر يناير</p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'>7- الأسعار علاه صالحة لمدة عام واحد من تاريخ التنشيط . بمجرد التوقيع . تصبح الاتفاقية سارية لمدة عام واحد تبدأ من تاريخ توقيع الاتفاقية من قبل التاجر.</p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'>8- الراسل مسؤول عن ضمان تعبئة الشحنة بشكل صحيح لمنع الضرر . لن تكون <strong>envio&nbsp;</strong>مسؤوة عن التلف الناتج عن سوء التغليف.</p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'><strong>التأمين :&nbsp;</strong></p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'>هناك خيارا ت تأمين مختلفة لطلباتك في حالة التلف ( برجاء مراجعة القسم 1.5 ) أو الضياع . يرجى اختيار إحدى الباقات التالية:&nbsp;</p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'>1- لتغطية 100%. سيكون قسط التأمين 1% من التكلفة الإجمالية للمنتج في الشهر.</p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'>2- لتغطية 75% . سيكون قسط التأمين 0.5% من التكلفة الإجمالية للمنتج في الشهر.</p>\n" +
                "<p dir=\"rtl\" style='margin: 0px 0px 8px; text-align: right; font-stretch: normal; font-size: 11px; line-height: normal; font-family: \"Times New Roman\";'>3- لا يوجد تأمين . مسؤولية envio هي 200 جنيه مصري بحد أقصى لكل شحنة في حالة التلف أو ألضياع.</p>\n" +
                "<p><br></p>";

        // get the terms of the app
        txtTerms.setText(HtmlCompat.fromHtml(privcyCode, 0));

        // Tool Bar Title
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("الشروط و الاحكام");
    }
}
