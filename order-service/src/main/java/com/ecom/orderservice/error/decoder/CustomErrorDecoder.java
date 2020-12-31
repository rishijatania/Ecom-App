// package com.ecom.orderservice.error.decoder;

// import java.text.DateFormat;
// import java.text.SimpleDateFormat;
// import java.util.Date;

// import feign.Response;
// import feign.codec.ErrorDecoder;

// public class CustomErrorDecoder implements ErrorDecoder {
// 	@Override
//     public Exception decode(String methodKey, Response response) {

//         switch (response.status()){
//             // case 400:
//             //     return new BadRequestException(response.body().);
//             // case 404:
//             //     return new NotFoundException();
//             default:
//                 return new Exception("Generic error");
//         }
//     }
	
// 	public String DateToString() {
// 		Date date = new Date();
// 		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
// 		return dateFormat.format(date);
// 	}
// }
