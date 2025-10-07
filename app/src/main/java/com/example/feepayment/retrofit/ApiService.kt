package com.example.feepayment.retrofit

import com.example.feepayment.responses.AdminHomeResponse
import com.example.feepayment.responses.AdminRequestResponse
import com.example.feepayment.responses.BusConfirmResponse
import com.example.feepayment.responses.BusPassResponse
import com.example.feepayment.responses.BusResponse
import com.example.feepayment.responses.DBstudentsResponse
import com.example.feepayment.responses.DueListResponse
import com.example.feepayment.responses.FeesDueResponse
import com.example.feepayment.responses.GenericResponse
import com.example.feepayment.responses.ImposeFeeSubmitResponse
import com.example.feepayment.responses.ImposefeeResponse
import com.example.feepayment.responses.InchargeResponse
import com.example.feepayment.responses.LoginResponse
import com.example.feepayment.responses.NotificationResponse
import com.example.feepayment.responses.PaySuccessResponse
import com.example.feepayment.responses.PaymentDetailsResponse
import com.example.feepayment.responses.PaymentHistoryResponse
import com.example.feepayment.responses.QuotaResponse
import com.example.feepayment.responses.ReceiptResponse
import com.example.feepayment.responses.RequestResponse
import com.example.feepayment.responses.SplitInstallmentResponse
import com.example.feepayment.responses.StatusResponse
import com.example.feepayment.responses.StudentHomeResponse
import com.example.feepayment.responses.StudentProfileResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("login.php")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("studhome.php")
    fun getStudentHomeData(
        @Field("studentid") studentId: Int
    ): Call<StudentHomeResponse>

    @FormUrlEncoded
    @POST("adminhome.php")
    fun getAdminDashboard(
        @Field("studentid") studentId: Int
    ): Call<AdminHomeResponse>

    @FormUrlEncoded
    @POST("profile.php")
    fun getStudentProfile(
        @Field("studentid") studentId: Int
    ): Call<StudentProfileResponse>


    @FormUrlEncoded
    @POST("paymenthistory.php")
    fun getPaymentHistory(
        @Field("studentid") studentid: Int
    ): Call<PaymentHistoryResponse>

    @FormUrlEncoded
    @POST("receiptpage.php")
    fun getReceiptDetails(
        @Field("studentid") studentid: Int,
        @Field("feename") feename: String
    ): Call<ReceiptResponse>


    @FormUrlEncoded
    @POST("studentduelist.php")
    fun getDueList(
        @Field("studentid") studentid: Int
    ): Call<DueListResponse>


    @FormUrlEncoded
    @POST("paymentpage.php")
    fun getPaymentDetails(
        @Field("studentid") studentid: Int,
        @Field("feename") feename: String
    ): Call<PaymentDetailsResponse>


    @FormUrlEncoded
    @POST("DBstudents.php")
    fun getStudentsByClass(
        @Field("class") classNumber: Int
    ): Call<DBstudentsResponse>

    @FormUrlEncoded
    @POST("defaulterDB.php")
    fun getDueStudentsByClass(
        @Field("class") classNumber: Int
    ): Call<DBstudentsResponse>


    @FormUrlEncoded
    @POST("fetchdue.php")
    fun getduefeeslist(
        @Field("studentid") studentid: Int
    ): Call<FeesDueResponse>

    @FormUrlEncoded
    @POST("getfullonlyduelist.php")
    fun getfullonlyduefeeslist(
        @Field("studentid") studentid: Int
    ): Call<DueListResponse>


    @FormUrlEncoded
    @POST("installment.php")
    fun splitInstallment(
        @Field("studentid") studentId: Int,
        @Field("feename") feeName: String,
        @Field("months") months: Int
    ): Call<SplitInstallmentResponse>



    @GET("quotas.php")
    fun getQuotaList(@Query("studentid") studentId: Int
    ): Call<QuotaResponse>

    @GET("incharge.php")
    fun getInchargeList(
    ): Call<InchargeResponse>

    @GET("get_busses.php")  // <-- you // r PHP file
    fun getBuses(): Call<BusResponse>

    @GET("singlebus.php")  // PHP file for single bus
    fun getBusDetails(
        @Query("routename") routename: String
    ): Call<BusResponse>


        @FormUrlEncoded
        @POST("getbuspass.php")
        fun getBusPass(
            @Field("studentid") studentId: Int
        ): Call<BusPassResponse>


    @POST("get_requests.php")
    @FormUrlEncoded
    fun getRequests(
        @Field("studentid") studentId: Int
    ): Call<RequestResponse>


    @GET("getallrequests.php")   // your PHP file
    fun getBusRequests(): Call<AdminRequestResponse>


    @FormUrlEncoded
    @POST("busrequest.php")
    fun bookBus(
        @Field("studentid") studentId: Int,
        @Field("routename") routename: String,
        @Field("amount") amount: Int,
        @Field("via") via: String,
        @Field("boarding_point") boardingPoint: String
    ): Call<BusConfirmResponse>


    @FormUrlEncoded
    @POST("scholarship.php")
    fun applyScholarship(
        @Field("studentid") studentId: Int,
        @Field("schname") schname: String,
        @Field("percentage") percentage: String,
        @Field("feename") feename: String,
        @Field("incharge") incharge: String
    ): Call<GenericResponse>


    @FormUrlEncoded
    @POST("imposefee2.php")
    fun getStudentsByIds(
        @Field("id[]") ids: List<Int>
    ): Call<ImposefeeResponse>



    @FormUrlEncoded
    @POST("imposingthefee.php")   // your PHP filename
    fun imposeFee(
        @Field("feename") feename: String,
        @Field("feeamt") feeamt: String,
        @Field("duedate") duedate: String,
        @Field("studentids") studentids: String   // JSON Array string
    ): Call<ImposeFeeSubmitResponse>



    @FormUrlEncoded
    @POST("updatebusstatus.php")
    fun updateBusRequest(
        @Field("studentid") studentId: Int,
        @Field("routename") routename: String,
        @Field("status") status: String
    ): Call<StatusResponse>


    @FormUrlEncoded
    @POST("paysuccess.php")
    fun updatePaymentSuccess(
        @Field("studentid") studentId: Int,
        @Field("feename") feename: String
    ): Call<PaySuccessResponse>



    @FormUrlEncoded
        @POST("notification.php")
        fun getnotifications(
            @Field("studentid") studentId: Int
        ): Call<NotificationResponse>

}
