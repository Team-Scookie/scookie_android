package com.example.scookie.network

interface NetworkService {

}

/* 예시

    /**
     * @header token
     * @param flag
     *  0 -> 전체보기
     *  1 -> 크리에이터
     *  2 -> 에디터
     *  3 -> 번역가
     *  4 -> 기타
     */
    @GET("profile/list/{flag}")
    fun getProfileLookUp(
        @Header("token") token: String,
        @Path("flag") flag: Int
    ): Call<GetProfileLookUpResponse>


    /**
     * 1대1 문의
     * @header token
     * @body comment
     */
    @POST("mypage/question")
    fun postManToManQusetion(
        @Header("Token") token: String,
        @Body postManToManQuestionRequest: PostManToManQuestionRequest
    ): Call<PickResponse>

    /**
     * 픽 삭제
     * @header token
     * @body nickname
     */
    @HTTP(method = "DELETE", path = "pick", hasBody = true)
    fun deletePick(
        @Header("token") token: String,
        @Body pickDTO: PickDTO
    ): Call<PickResponse>


    /**
     * 회원정보 변경
     * @header token
     */
    @Multipart
    @PUT("mypage/modify")
    fun modifyProfile(
        @Header("Token") token: String,
        @Part user_img: MultipartBody.Part?,
        @Part("user_nickname") userNickname: RequestBody,
        @Part("user_number") userNumber: RequestBody,
        @Part("user_pw") userPw: RequestBody,
        @Part("new_pw") newPw: RequestBody,
        @Part("user_type") userType: Int
    ): Call<ModifyProfileResponse>
 */

