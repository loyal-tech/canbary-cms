package com.adopt.apigw.interceptor;

public class CustomManagerInterceptor {/*implements HandlerInterceptor{
	 @Override
	   public boolean preHandle(
	     HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		 String strValidateSesson=(String) request.getSession().getAttribute("SESSIONDETAIL");
		  if(strValidateSesson!=null) {
			  return true;
		  }
		  else
		  {
  		    logger.info("In Pre Handler Interceptor:"+request.getRequestURI().toString()+":Session:"+strValidateSesson);
		    logger.info("Invalid Session");
		    response.sendRedirect("/login");
   		    return false;
		  }
	   }
	 
	   @Override
	   public void postHandle(
	      HttpServletRequest request, HttpServletResponse response, Object handler, 
	      ModelAndView modelAndView) throws Exception {}
	   
	   @Override
	   public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
	      Object handler, Exception exception) throws Exception {}
*/	   
}
