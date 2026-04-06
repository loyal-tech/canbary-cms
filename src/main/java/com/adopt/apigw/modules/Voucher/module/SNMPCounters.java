package com.adopt.apigw.modules.Voucher.module;

public class SNMPCounters {

	public static int createQosSuccess = 0;
	public static int createQosFailure = 0;
	public static int updateQosSuccess = 0;
	public static int updateQosFailure = 0;
	public static int deleteQosSuccess = 0;
	public static int deleteQosFailure = 0;
	public static int getQosListSuccess = 0;
	public static int getQosListFailure = 0;
	public static int searchQosByNameSuccess = 0;
	public static int searchQosByNameFailure = 0;
	public static int changeStatusQosSuccess = 0;
	public static int changeStatusQosFailure = 0;

	//Location Master
	private static int createLocationSuccess = 0;
	private static int createLocationFailure = 0;
	private static int updateLocationSuccess = 0;
	private static int updateLocationFailure = 0;
	private static int deleteLocationSuccess = 0;
	private static int deleteLocationFailure = 0;
	private static int getLocationListSuccess = 0;
	private static int getLocationListFailure = 0;
	private static int searchLocationByNameSuccess = 0;
	private static int searchLocationByNameFailure = 0;
	private static int changeStatusLocationSuccess = 0;
	private static int changeStatusLocationFailure = 0;
	private static int getLocationByIdSuccess = 0;
	private static int getLocationByIdFailure = 0;
	private static int getActiveLocationListSuccess = 0;
	private static int getActiveLocationListFailure = 0;

	//Plan
	private static int createPlanSuccess = 0;
	private static int createPlanFailure = 0;
	private static int updatePlanSuccess = 0;
	private static int updatePlanFailure = 0;
	private static int deletePlanSuccess = 0;
	private static int deletePlanFailure = 0;
	private static int getPlanListSuccess = 0;
	private static int getPlanListFailure = 0;
	private static int searchPlanByNameSuccess = 0;
	private static int searchPlanByNameFailure = 0;
	private static int changeStatusPlanSuccess = 0;
	private static int changeStatusPlanFailure = 0;
	private static int getPlanByIdSuccess = 0;
	private static int getPlanByIdFailure = 0;
	private static int getActivePlanListSuccess = 0;
	private static int getActivePlanListFailure = 0;
	private static int getValidPlanListSuccess = 0;
	private static int getValidPlanListFailure = 0;

	//Voucher Profile
	private static int createVoucherProfileSuccess = 0;
	private static int createVoucherProfileFailure = 0;
	private static int updateVoucherProfileSuccess = 0;
	private static int updateVoucherProfileFailure = 0;
	private static int deleteVoucherProfileSuccess = 0;
	private static int deleteVoucherProfileFailure = 0;
	private static int getVoucherProfileListSuccess = 0;
	private static int getVoucherProfileListFailure = 0;
	private static int searchVoucherProfileByNameSuccess = 0;
	private static int searchVoucherProfileByNameFailure = 0;
	private static int changeStatusVoucherProfileSuccess = 0;
	private static int changeStatusVoucherProfileFailure = 0;
	private static int getVoucherProfileByIdSuccess = 0;
	private static int getVoucherProfileByIdFailure = 0;

	//Concurrent Policy
	private static int createConcurrentPolicySuccess = 0;
	private static int createConcurrentPolicyFailure = 0;
	private static int updateConcurrentPolicySuccess = 0;
	private static int updateConcurrentPolicyFailure = 0;
	private static int deleteConcurrentPolicySuccess = 0;
	private static int deleteConcurrentPolicyFailure = 0;
	private static int getConcurrentPolicyListSuccess = 0;
	private static int getConcurrentPolicyListFailure = 0;
	private static int searchConcurrentPolicyByNameSuccess = 0;
	private static int searchConcurrentPolicyByNameFailure = 0;
	private static int changeStatusConcurrentPolicySuccess = 0;
	private static int changeStatusConcurrentPolicyFailure = 0;
	private static int getConcurrentPolicyByIdSuccess = 0;
	private static int getConcurrentPolicyByIdFailure = 0;
	private static int getActiveConcurrentPolicyListSuccess = 0;
	private static int getActiveConcurrentPolicyListFailure = 0;

	//Customer
	private static int createCustomerSuccess = 0;
	private static int createCustomerFailure = 0;
	private static int updateCustomerSuccess = 0;
	private static int updateCustomerFailure = 0;
	private static int deleteCustomerSuccess = 0;
	private static int deleteCustomerFailure = 0;
	private static int getCustomerListSuccess = 0;
	private static int getCustomerListFailure = 0;
	private static int searchCustomerByNameSuccess = 0;
	private static int searchCustomerByNameFailure = 0;
	private static int changeStatusCustomerSuccess = 0;
	private static int changeStatusCustomerFailure = 0;
	private static int getCustomerByIdSuccess = 0;
	private static int getCustomerByIdFailure = 0;
	private static int getCustomerByCidMacSuccess = 0;
	private static int getCustomerByCidMacFailure = 0;
	private static int rechargeCustomerQuotaSuccess = 0;
	private static int rechargeCustomerQuotaFailure = 0;
	private static int changeCustomerPasswordSuccess = 0;
	private static int changeCustomerPasswordFailure = 0;
	private static int getCustomerPlanDetailsSuccess = 0;
	private static int getCustomerPlanDetailsFailure = 0;
	private static int exportCustomerListSuccess = 0;
	private static int exportCustomerListFailure = 0;
	private static int loginCustomerSuccess = 0;
	private static int loginCustomerFailure = 0;
	private static int logoutCustomerSuccess = 0;
	private static int logoutCustomerFailure = 0;
	private static int renewCustomerSuccess = 0;
	private static int renewCustomerFailure = 0;

	//TimeBase Policy
	private static int getTimeBasePolicyListSuccess=0;
	private static int getTimeBasePolicyListFailure=0;
	private static int createTimeBasePolicySuccess=0;
	private static int createTimeBasePolicyFailure=0;
	private static int deleteTimeBasePolicySuccess=0;
	private static int deleteTimeBasePolicyFailure=0;
	private static int updateTimeBasePolicySuccess=0;
	private static int updateTimeBasePolicyFailure=0;
	private static int changeStatusTimeBasePolicySuccess=0;
	private static int changeStatusTimeBasePolicyFailure=0;
	private static int findAllTimeBasePolicySuccess=0;
	private static int findAllTimeBasePolicyFailure=0;
	private static int findByIdTimeBasePolicySuccess=0;
	private static int findByIdTimeBasePolicyFailure=0;
	private static int findAllActiveTimeBasePolicySuccess=0;
	private static int findAllActiveTimeBasePolicyFailure=0;

	//Otp Management
	private static int getOtpManagmentListSuccess=0;
	private static int getOtpManagmentListFailure=0;
	private static int createOtpManagmentSuccess=0;
	private static int createOtpManagmentFailure=0;
	private static int updateOtpManagmentSuccess=0;
	private static int updateOtpManagmentFailure=0;
	private static int deleteOtpManagmentSuccess=0;
	private static int deleteOtpManagmentFailure=0;
	private static int findByProfileIdOtpManagmentSuccess=0;
	private static int findByProfileIdOtpManagmentFailure=0;
	private static int findByProfileNameOtpManagmentSuccess=0;
	private static int findByProfileNameOtpManagmentFailure=0;

	//Partner Management
	private static int findAllPartnerListSuccess=0;
	private static int findAllPartnerListFailure=0;
	private static int createPartnerListSuccess=0;
	private static int createPartnerListFailure=0;
	private static int deletePartnerListSuccess=0;
	private static int deletePartnerListFailure=0;
	private static int updatePartnerListSuccess=0;
	private static int updatePartnerListFailure=0;
	private static int findPartnerByIdSuccess=0;
	private static int findPartnerByIdFailure=0;
	private static int findDistributeOfPartnerListSuccess=0;
	private static int findDistributeOfPartnerListFailure=0;

	//Template managment
	private static int findAllTemplateListSuccess=0;
	private static int findAllTemplateListFailure=0;
	private static int deleteTemplateSuccess=0;
	private static int deleteTemplateFailure=0;
	private static int createTemplateSuccess=0;
	private static int createTemplateFailure=0;
	private static int updateTemplateSuccess=0;
	private static int updateTemplateFailure=0;
	private static int searchTemplateSuccess=0;
	private static int searchTemplateFailure=0;

	//Reseller Management
	private static int getAllResellerListSuccess=0;
	private static int getAllResellerListFailure=0;
	private static int findAllResellerListSuccess=0;
	private static int findAllResellerListFailure=0;
	private static int findResellerByIdSuccess=0;
	private static int findResellerByIdFailure=0;
	private static int createResellerSuccess=0;
	private static int createResellerFailure=0;
	private static int createManageBalanceForResellerSuccess=0;
	private static int createManageBalanceForResellerFailure=0;
	private static int createAddBalanceForResellerSuccess=0;
	private static int createAddBalanceForResellerFailure=0;
	private static int updateResellerSuccess=0;
	private static int updateResellerFailure=0;
	private static int deleteResellerSuccess=0;
	private static int deleteResellerFailure=0;
	private static int searchResellerListSuccess=0;
	private static int searchResellerListFailure=0;
	private static int validLoginUserForResellerSuccess=0;
	private static int validLoginUserForResellerFailure=0;
	private static int changeResellerPasswordSuccess=0;
	private static int changeResellerPasswordFailure=0;
	private static int searchResellerByLocationIdListSuccess=0;
	private static int searchResellerByLocationIdListFailure=0;
	private static int changeResellerStatusSuccess=0;
	private static int changeResellerStatusFailure=0;

	//Voucher Management
	public static int validateVoucherSuccess=0;
	public static int validateVoucherFailure=0;
	public static int getAllVouchersListSuccess=0;
	public static int getAllVouchersListFailure=0;
	public static int findVouchersByBatchIdSuccess=0;
	public static int findVouchersByBatchIdFailure=0;
	public static int findVouchersSuccess=0;
	public static int findVouchersFailure=0;
	public static int createVoucherIdSuccess=0;
	public static int createVoucherIdFailure=0;
	public static int changeVoucherStatusToActiveSuccess=0;
	public static int changeVoucherStatusToActiveFailure=0;
	public static int changeVoucherStatusToBlockSuccess=0;
	public static int changeVoucherStatusToBlockFailure=0;
	public static int changeVoucherStatusToUnblockSuccess=0;
	public static int changeVoucherStatusToUnblockFailure=0;
	public static int changeVoucherStatusToScrapSuccess=0;
	public static int changeVoucherStatusToScrapFailure=0;
	public static int sendSMSForVucherSuccess=0;
	public static int sendSMSForVucherFailure=0;

	private static int createVoucherBatchSuccess=0;
	private static int createVoucherBatchFailure=0;
	private static int updateVoucherBatchSuccess=0;
	private static int updateVoucherBatchFailure=0;
	private static int deleteVoucherBatchSuccess=0;
	private static int deleteVoucherBatchFailure=0;
	private static int findAllVoucherBatchSuccess=0;
	private static int findAllVoucherBatchFailure=0;
	private static int getVoucherBatchListSuccess=0;
	private static int getVoucherBatchListFailure=0;
	private static int findVoucherBatchByIdSuccess=0;
	private static int findVoucherBatchByIdFailure=0;
	private static int findVoucherBatchWithoutResellerSuccess=0;
	private static int findVoucherBatchWithoutResellerFailure=0;
	private static int searchVoucherBatchByDateSuccess=0;
	private static int searchVoucherBatchByDateFailure=0;
	private static int assignResellertoVoucherBatchSuccess=0;
	private static int assignResellertoVoucherBatchFailure=0;
	private static int findAllVoucherSuccess=0;
	private static int findAllVoucherFailure=0;
	private static int generateBatchandVoucherSuccess=0;
	private static int generateBatchandVoucherFailure=0;



	public void incrementCreateQosSuccess() {
		createQosSuccess++;
	}

	public void incrementCreateQosFailure() {
		createQosFailure++;
	}

	public void incrementUpdateQosSuccess() {
		updateQosSuccess++;
	}

	public void incrementUpdateQosFailure() {
		updateQosFailure++;
	}

	public void incrementDeleteQosSuccess() {
		deleteQosSuccess++;
	}

	public void incrementDeleteQosFailure() {
		deleteQosFailure++;
	}

	public void incrementGetQosListSuccess() {
		getQosListSuccess++;
	}

	public void incrementGetQosListFailure() {
		getQosListFailure++;
	}
	public void incrementSearchQosByNameSuccess() {
		searchQosByNameSuccess++;
	}

	public void incrementSearchQosByNameFailure() {
		searchQosByNameFailure++;
	}

	public void incrementChangeStatusQosSuccess() {
		changeStatusQosSuccess++;
	}

	public void incrementChangeStatusQosFailure() {
		changeStatusQosFailure++;
	}

	public void incrementCreateLocationSuccess() { createLocationSuccess++; }
	public void incrementCreateLocationFailure() { createLocationFailure++; }
	public void incrementUpdateLocationSuccess() { updateLocationSuccess++; }
	public void incrementUpdateLocationFailure() { updateLocationFailure++; }
	public void incrementDeleteLocationSuccess() { deleteLocationSuccess++; }
	public void incrementDeleteLocationFailure() { deleteLocationFailure++; }
	public void incrementGetLocationListSuccess() { getLocationListSuccess++; }
	public void incrementGetLocationListFailure() { getLocationListFailure++; }
	public void incrementSearchLocationByNameSuccess() { searchLocationByNameSuccess++; }
	public void incrementSearchLocationByNameFailure() { searchLocationByNameFailure++; }
	public void incrementSearchLocationByIdSuccess() { getLocationByIdSuccess++; }
	public void incrementSearchLocationByIdFailure() { getLocationByIdFailure++; }
	public void incrementGetActiveLocationListSuccess() { getActiveLocationListSuccess++; }
	public void incrementGetActiveLocationListFailure() { getActiveLocationListFailure++; }
	public void incrementChangeStatusLocationSuccess() { changeStatusLocationSuccess++; }
	public void incrementChangeStatusLocationFailure() { changeStatusLocationFailure++; }

	public void incrementCreatePlanSuccess() { createPlanSuccess++; }
	public void incrementCreatePlanFailure() { createPlanFailure++; }
	public void incrementUpdatePlanSuccess() { updatePlanSuccess++; }
	public void incrementUpdatePlanFailure() { updatePlanFailure++; }
	public void incrementDeletePlanSuccess() { deletePlanSuccess++; }
	public void incrementDeletePlanFailure() { deletePlanFailure++; }
	public void incrementGetPlanListSuccess() { getPlanListSuccess++; }
	public void incrementGetPlanListFailure() { getPlanListFailure++; }
	public void incrementSearchPlanByNameSuccess() { searchPlanByNameSuccess++; }
	public void incrementSearchPlanByNameFailure() { searchPlanByNameFailure++; }
	public void incrementSearchPlanByIdSuccess() { getPlanByIdSuccess++; }
	public void incrementSearchPlanByIdFailure() { getPlanByIdFailure++; }
	public void incrementGetActivePlanListSuccess() { getActivePlanListSuccess++; }
	public void incrementGetActivePlanListFailure() { getActivePlanListFailure++; }
	public void incrementGetValidPlanListSuccess() { getValidPlanListSuccess++; }
	public void incrementGetValidPlanListFailure() { getValidPlanListFailure++; }
	public void incrementChangeStatusPlanSuccess() { changeStatusPlanSuccess++; }
	public void incrementChangeStatusPlanFailure() { changeStatusPlanFailure++; }

	public void incrementCreateVoucherProfileSuccess() { createVoucherProfileSuccess++; }
	public void incrementCreateVoucherProfileFailure() { createVoucherProfileFailure++; }
	public void incrementUpdateVoucherProfileSuccess() { updateVoucherProfileSuccess++; }
	public void incrementUpdateVoucherProfileFailure() { updateVoucherProfileFailure++; }
	public void incrementDeleteVoucherProfileSuccess() { deleteVoucherProfileSuccess++; }
	public void incrementDeleteVoucherProfileFailure() { deleteVoucherProfileFailure++; }
	public void incrementGetVoucherProfileListSuccess() { getVoucherProfileListSuccess++; }
	public void incrementGetVoucherProfileListFailure() { getVoucherProfileListFailure++; }
	public void incrementSearchVoucherProfileByNameSuccess() { searchVoucherProfileByNameSuccess++; }
	public void incrementSearchVoucherProfileByNameFailure() { searchVoucherProfileByNameFailure++; }
	public void incrementSearchVoucherProfileByIdSuccess() { getVoucherProfileByIdSuccess++; }
	public void incrementSearchVoucherProfileByIdFailure() { getVoucherProfileByIdFailure++; }
	public void incrementChangeStatusVoucherProfileSuccess() { changeStatusVoucherProfileSuccess++; }
	public void incrementChangeStatusVoucherProfileFailure() { changeStatusVoucherProfileFailure++; }

	public void incrementCreateConcurrentPolicySuccess() { createConcurrentPolicySuccess++; }
	public void incrementCreateConcurrentPolicyFailure() { createConcurrentPolicyFailure++; }
	public void incrementUpdateConcurrentPolicySuccess() { updateConcurrentPolicySuccess++; }
	public void incrementUpdateConcurrentPolicyFailure() { updateConcurrentPolicyFailure++; }
	public void incrementDeleteConcurrentPolicySuccess() { deleteConcurrentPolicySuccess++; }
	public void incrementDeleteConcurrentPolicyFailure() { deleteConcurrentPolicyFailure++; }
	public void incrementGetConcurrentPolicyListSuccess() { getConcurrentPolicyListSuccess++; }
	public void incrementGetConcurrentPolicyListFailure() { getConcurrentPolicyListFailure++; }
	public void incrementSearchConcurrentPolicyByNameSuccess() { searchConcurrentPolicyByNameSuccess++; }
	public void incrementSearchConcurrentPolicyByNameFailure() { searchConcurrentPolicyByNameFailure++; }
	public void incrementSearchConcurrentPolicyByIdSuccess() { getConcurrentPolicyByIdSuccess++; }
	public void incrementSearchConcurrentPolicyByIdFailure() { getConcurrentPolicyByIdFailure++; }
	public void incrementChangeStatusConcurrentPolicySuccess() { changeStatusConcurrentPolicySuccess++; }
	public void incrementChangeStatusConcurrentPolicyFailure() { changeStatusConcurrentPolicyFailure++; }
	public void incrementGetActiveConcurrentPolicyListSuccess() { getActiveConcurrentPolicyListSuccess++; }
	public void incrementGetActiveConcurrentPolicyListFailure() { getActiveConcurrentPolicyListFailure++; }

	public void incrementCreateCustomerSuccess() { createCustomerSuccess++; }
	public void incrementCreateCustomerFailure() { createCustomerFailure++; }
	public void incrementUpdateCustomerSuccess() { updateCustomerSuccess++; }
	public void incrementUpdateCustomerFailure() { updateCustomerFailure++; }
	public void incrementDeleteCustomerSuccess() { deleteCustomerSuccess++; }
	public void incrementDeleteCustomerFailure() { deleteCustomerFailure++; }
	public void incrementGetCustomerListSuccess() { getCustomerListSuccess++; }
	public void incrementGetCustomerListFailure() { getCustomerListFailure++; }
	public void incrementSearchCustomerByNameSuccess() { searchCustomerByNameSuccess++; }
	public void incrementSearchCustomerByNameFailure() { searchCustomerByNameFailure++; }
	public void incrementSearchCustomerByIdSuccess() { getCustomerByIdSuccess++; }
	public void incrementSearchCustomerByIdFailure() { getCustomerByIdFailure++; }
	public void incrementChangeStatusCustomerSuccess() { changeStatusCustomerSuccess++; }
	public void incrementChangeStatusCustomerFailure() { changeStatusCustomerFailure++; }
	public void incrementGetCustomerByCidMacSuccess() { getCustomerByCidMacSuccess++; }
	public void incrementGetCustomerByCidMacFailure() { getCustomerByCidMacFailure++; }
	public void incrementRechargeCustomerQuotaSuccess() { rechargeCustomerQuotaSuccess++; }
	public void incrementRechargeCustomerQuotaFailure() { rechargeCustomerQuotaFailure++; }
	public void incrementChangeCustomerPasswordSuccess() { changeCustomerPasswordSuccess++; }
	public void incrementChangeCustomerPasswordFailure() { changeCustomerPasswordFailure++; }
	public void incrementGetCustomerPlanDetailsSuccess() { getCustomerPlanDetailsSuccess++; }
	public void incrementGetCustomerPlanDetailsFailure() { getCustomerPlanDetailsFailure++; }
	public void incrementExportCustomerListSuccess() { exportCustomerListSuccess++; }
	public void incrementExportCustomerListFailure() { exportCustomerListFailure++; }
	public void incrementLoginCustomerSuccess() { loginCustomerSuccess++; }
	public void incrementLoginCustomerFailure() { loginCustomerFailure++; }
	public void incrementLogoutCustomerSuccess() { logoutCustomerSuccess++; }
	public void incrementLogoutCustomerFailure() { logoutCustomerFailure++; }
	public void incrementRenewCustomerSuccess() { renewCustomerSuccess++; }
	public void incrementRenewCustomerFailure() { renewCustomerFailure++; }


	//Time base policy
	public void incrementTimeBasePolicyListSuccess() {
		getTimeBasePolicyListSuccess++;
	}
	public void incrementTimeBasePolicyListFailure() {
		getTimeBasePolicyListFailure++;
	}
	public void incrementCreateTimeBasePolicySuccess(){
		createTimeBasePolicySuccess++;
	}
	public void incrementCreateTimeBasePolicyFailure(){
		createTimeBasePolicyFailure++;
	}
	public void incrementDeleteTimeBasePolicySuccess(){
		deleteTimeBasePolicySuccess++;
	}
	public void incrementDeleteTimeBasePolicyFailure(){
		deleteTimeBasePolicyFailure++;
	}
	public void incrementUpdateTimeBasePolicySuccess(){
		updateTimeBasePolicySuccess++;
	}
	public void incrementUpdateTimeBasePolicyFailure(){
		updateTimeBasePolicyFailure++;
	}
	public void incrementChangeStatusTimeBasePolicySuccess(){
		changeStatusTimeBasePolicySuccess++;
	}
	public void incrementChangeStatusTimeBasePolicyFailure(){
		changeStatusTimeBasePolicyFailure++;
	}
	public void incrementFindAllTimeBasePolicySuccess(){
		findAllTimeBasePolicySuccess++;
	}
	public void incrementFindAllTimeBasePolicyFailure(){
		findAllTimeBasePolicyFailure++;
	}
	public void incrementfindByIdTimeBasePolicySuccess() {
		findByIdTimeBasePolicySuccess++;
	}
	public void incrementfindByIdTimeBasePolicyFailure() {
		findByIdTimeBasePolicyFailure++;
	}
	public void incrementFindAllActiveTimeBasePolicySuccess() {
		findAllActiveTimeBasePolicySuccess++;
	}
	public void incrementFindAllActiveTimeBasePolicyFailure() {
		findAllActiveTimeBasePolicyFailure++;
	}

	//OTP Management
	public void incrementGetOtpManagmentSuccess(){
		getOtpManagmentListSuccess++;
	}
	public void incrementGetOtpManagmentFailure(){
		getOtpManagmentListFailure++;
	}
	public void incrementCreateOtpManagmentSuccess(){
		 createOtpManagmentSuccess++;
	}
	public void incrementCreateOtpManagmentFailure(){
		 createOtpManagmentFailure++;
	}
	public void incrementUpdateOtpManagmentSuccess(){
		 updateOtpManagmentSuccess++;
	}
	public void incrementUpdateOtpManagmentFailure(){
		updateOtpManagmentFailure++;
	}
	public void incrementDeleteOtpManagmentSuccess(){
		 deleteOtpManagmentSuccess++;
	}
	public void incrementDeleteOtpManagmentFailure(){
		 deleteOtpManagmentFailure++;
	}
	public void incrementfindByProfileIdOtpManagmentSuccess(){
		 findByProfileIdOtpManagmentSuccess++;
	}
	public void incrementfindByProfileIdOtpManagmentFailure(){
		 findByProfileIdOtpManagmentFailure++;
	}
	public void incrementfindByProfileNameOtpManagmentSuccess(){
		findByProfileNameOtpManagmentSuccess++;
	}
	public void incrementfindByProfileNameOtpManagmentFailure(){
		findByProfileNameOtpManagmentFailure++;
	}

	//Partner Management
	public void incrementPartnerListSuccess() { findAllPartnerListSuccess++; }
	public void incrementPartnerListFailure() { findAllPartnerListFailure++; }
	public void incrementCreatePartnerListSuccess() { createPartnerListSuccess++; }
	public void incrementCreatePartnerListFailure() { createPartnerListFailure++; }
	public void incrementDeletePartnerListSuccess() { deletePartnerListSuccess++; }
	public void incrementDeletePartnerListFailure() { deletePartnerListFailure++; }
	public void incrementUpdatePartnerListSuccess() { updatePartnerListSuccess++; }
	public void incrementUpdatePartnerListFailure() { updatePartnerListFailure++; }
	public void incrementFindPartnerByIdSuccess() { findPartnerByIdSuccess++; }
	public void incrementFindPartnerByIdFailure() { findPartnerByIdFailure++; }
	public void incrementFindDistributerPartnerListSuccess() { findDistributeOfPartnerListSuccess++; }
	public void incrementFindDistributerPartnerListFailure() { findDistributeOfPartnerListFailure++; }

	//Template managment
	public void incrementTemplateListSuccess() { findAllTemplateListSuccess++; }
	public void incrementTemplateListFailure() { findAllTemplateListFailure++; }
	public void incrementDeleteTemplateSuccess() { deleteTemplateSuccess++; }
	public void incrementDeleteTemplateFailure() { deleteTemplateFailure++; }
	public void incrementCreateTemplateSuccess() { createTemplateSuccess++; }
	public void incrementCreateTemplateFailure() { createTemplateFailure++; }
	public void incrementUpdateTemplateSuccess() { updateTemplateSuccess++; }
	public void incrementUpdateTemplateFailure() { updateTemplateFailure++; }
	public void incrementSearchTemplateSuccess(){ searchTemplateSuccess++;}
	public void incrementSearchTemplateFailure(){ searchTemplateFailure++;}


	//Reseller Management
	public void incrementGetAllResellerListSuccess() { getAllResellerListSuccess++; }
	public void incrementGetAllResellerListFailure() { getAllResellerListFailure++; }
	public void incrementFindAllResellerListSuccess() { findAllResellerListSuccess++; }
	public void incrementFindAllResellerListFailure() { findAllResellerListFailure++; }
	public void incrementFindResellerByIdSuccess() { findResellerByIdSuccess++; }
	public void incrementFindResellerByIdFailure() { findResellerByIdFailure++; }
	public void incrementCreateResellerSuccess() { createResellerSuccess++; }
	public void incrementCreateResellerFailure() { createResellerFailure++; }
	public void incrementCreateManageBalanceForResellerSuccess() { createManageBalanceForResellerSuccess++; }
	public void incrementCreateManageBalanceForResellerFailure() { createManageBalanceForResellerFailure++; }
	public void incrementCreateAddBalanceForResellerSuccess() { createAddBalanceForResellerSuccess++; }
	public void incrementCreateAddBalanceForResellerFailure() { createAddBalanceForResellerFailure++; }
	public void incrementUpdateResellerSuccess() { updateResellerSuccess++; }
	public void incrementUpdateResellerFailure() { updateResellerFailure++; }
	public void incrementDeleteResellerSuccess() { deleteResellerSuccess++; }
	public void incrementDeleteResellerFailure() { deleteResellerFailure++; }
	public void incrementSearchResellerListSuccess() { searchResellerListSuccess++; }
	public void incrementSearchResellerListFailure() { searchResellerListFailure++; }
	public void incrementValidLoginUserForResellerSuccess() { validLoginUserForResellerSuccess++; }
	public void incrementValidLoginUserForResellerFailure() { validLoginUserForResellerFailure++; }
	public void incrementChangeResellerPasswordSuccess() { changeResellerPasswordSuccess++; }
	public void incrementChangeResellerPasswordFailure() { changeResellerPasswordFailure++; }
	public void incrementSearchResellerByLocationIdListSuccess() { searchResellerByLocationIdListSuccess++; }
	public void incrementSearchResellerByLocationIdListFailure() { searchResellerByLocationIdListFailure++; }
	public void incrementChangeResellerStatusSuccess() { changeResellerStatusSuccess++; }
	public void incrementChangeResellerStatusFailure() { changeResellerStatusFailure++; }

	//Voucher Management
	public void incrementValidateVoucherSuccess() { validateVoucherSuccess++; }
	public void incrementValidateVoucherFailure() { validateVoucherFailure++; }
	public void incrementGetAllVouchersListSuccess() { getAllVouchersListSuccess++; }
	public void incrementGetAllVouchersListFailure() { getAllVouchersListFailure++; }
	public void incrementFindVouchersByBatchIdSuccess() { findVouchersByBatchIdSuccess++; }
	public void incrementFindVouchersByBatchIdFailure() { findVouchersByBatchIdFailure++; }
	public void incrementFindVouchersSuccess() { findVouchersSuccess++; }
	public void incrementFindVouchersFailure() { findVouchersFailure++; }
	public void incrementCreateVoucherIdSuccess() { createVoucherIdSuccess++; }
	public void incrementCreateVoucherIdFailure() { createVoucherIdFailure++; }
	public void incrementChangeVoucherStatusToActiveSuccess() { changeVoucherStatusToActiveSuccess++; }
	public void incrementChangeVoucherStatusToActiveFailure() { changeVoucherStatusToActiveFailure++; }
	public void incrementChangeVoucherStatusToBlockSuccess() { changeVoucherStatusToBlockSuccess++; }
	public void incrementChangeVoucherStatusToBlockFailure() { changeVoucherStatusToBlockFailure++; }
	public void incrementChangeVoucherStatusToUnblockSuccess() { changeVoucherStatusToUnblockSuccess++; }
	public void incrementChangeVoucherStatusToUnblockFailure() { changeVoucherStatusToUnblockFailure++; }
	public void incrementChangeVoucherStatusToScrapSuccess() { changeVoucherStatusToScrapSuccess++; }
	public void incrementChangeVoucherStatusToScrapFailure() { changeVoucherStatusToScrapFailure++; }
	public void incrementSendSMSForVoucherSuccess() { sendSMSForVucherSuccess++; }
	public void incrementSendSMSForVoucherFailure() { sendSMSForVucherFailure++; }

	//VoucherBatch
	public void increamentCreateVoucherBatchSuccess() { createVoucherBatchSuccess++; }
	public void increamentCreateVoucherBatchFailure() { createVoucherBatchFailure++; }
	public void increamentUpdateVoucherBatchSuccess() {  updateVoucherBatchSuccess++; }
	public void increamentUpdateVoucherBatchFailure() {  updateVoucherBatchFailure++; }
	public void increamentDeleteVoucherBatchSuccess() {  deleteVoucherBatchSuccess++; }
	public void increamentDeleteVoucherBatchFailure() {  deleteVoucherBatchFailure++; }
	public void increamentfindAllVoucherBatchSuccess() {  findAllVoucherBatchSuccess++; }
	public void increamentfindAllVoucherBatchFailure() {   findAllVoucherBatchFailure++; }
	public void increamentgetAllVoucherBatchListSuccess() {  getVoucherBatchListSuccess++; }
	public void increamentgetAllVoucherBatchListFailure() {  getVoucherBatchListFailure++; }
	public void increamentfindVoucherBatchByIdSuccess() {   findVoucherBatchByIdSuccess++; }
	public void increamentfindVoucherBatchByIdFailure() {   findVoucherBatchByIdFailure++; }
	public void increamentVoucherBatchwithoutResellerSuccess() { findVoucherBatchWithoutResellerSuccess++; }
	public void increamentVoucherBatchwithoutResellerFailure() { findVoucherBatchWithoutResellerFailure++; }
	public void increamentSearchVoucherBatchByDateSuccess() {  searchVoucherBatchByDateSuccess++; }
	public void increamentSearchVoucherBatchByDateFailure() {  searchVoucherBatchByDateFailure++; }
	public void increamentAssignResellertoVoucherBatchSuccess() {  assignResellertoVoucherBatchSuccess++; }
	public void increamentAssignResellertoVoucherBatchFailure() {  assignResellertoVoucherBatchFailure++; }
	public void increamentfindAllVoucherSuccess() {  findAllVoucherSuccess++; }
	public void increamentfindAllVoucherFailure() {  findAllVoucherFailure++; }
	public void increamentgenerateBatchAndVoucherSuccess() {  generateBatchandVoucherSuccess++; }
	public void increamentgenerateBatchAndVoucherFailure() {  generateBatchandVoucherFailure++; }

	public static Integer getCountForValue(String value) {
		switch (value){
			case OIDConstant.CREATE_QOS_SUCCESS_OID: 							return createQosSuccess;
			case OIDConstant.CREATE_QOS_FAILURE_OID: 							return createQosFailure;
			case OIDConstant.UPDATE_QOS_SUCCESS_OID: 							return updateQosSuccess;
			case OIDConstant.UPDATE_QOS_FAILURE_OID: 							return updateQosFailure;
			case OIDConstant.DELETE_QOS_SUCCESS_OID: 							return deleteQosSuccess;
			case OIDConstant.DELETE_QOS_FAILURE_OID: 							return deleteQosFailure;
			case OIDConstant.GET_QOS_LIST_SUCCESS_OID: 							return getQosListSuccess;
			case OIDConstant.GET_QOS_LIST_FAILURE_OID: 							return getQosListFailure;
			case OIDConstant.SEARCH_QOS_BY_NAME_SUCCESS_OID: 					return searchQosByNameSuccess;
			case OIDConstant.SEARCH_QOS_BY_NAME_FAILURE_OID:				 	return searchQosByNameFailure;
			case OIDConstant.CHANGE_STATUS_QOS_SUCCESS_OID: 					return changeStatusQosSuccess;
			case OIDConstant.CHANGE_STATUS_QOS_FAILURE_OID: 					return changeStatusQosFailure;
			/*case OIDConstant.PDU_RESET:
				pdu.clear();
				break;*/
			case OIDConstant.CREATE_LOCATION_SUCCESS_OID: 						return createLocationSuccess;
			case OIDConstant.CREATE_LOCATION_FAILURE_OID: 						return createLocationFailure;
			case OIDConstant.UPDATE_LOCATION_SUCCESS_OID: 						return updateLocationSuccess;
			case OIDConstant.UPDATE_LOCATION_FAILURE_OID: 						return updateLocationFailure;
			case OIDConstant.DELETE_LOCATION_SUCCESS_OID: 						return deleteLocationSuccess;
			case OIDConstant.DELETE_LOCATION_FAILURE_OID: 						return deleteLocationFailure;
			case OIDConstant.GET_LOCATION_LIST_SUCCESS_OID: 					return getLocationListSuccess;
			case OIDConstant.GET_LOCATION_LIST_FAILURE_OID: 					return getLocationListFailure;
			case OIDConstant.SEARCH_LOCATION_BY_NAME_SUCCESS_OID: 				return searchLocationByNameSuccess;
			case OIDConstant.SEARCH_LOCATION_BY_NAME_FAILURE_OID: 				return searchLocationByNameFailure;
			case OIDConstant.GET_LOCATION_BY_ID_SUCCESS_OID: 					return getLocationByIdSuccess;
			case OIDConstant.GET_LOCATION_BY_ID_FAILURE_OID: 					return getLocationByIdFailure;
			case OIDConstant.CHANGE_STATUS_LOCATION_SUCCESS_OID: 				return changeStatusLocationSuccess;
			case OIDConstant.CHANGE_STATUS_LOCATION_FAILURE_OID: 				return changeStatusLocationFailure;
			case OIDConstant.GET_ACTIVE_LOCATION_LIST_SUCCESS_OID: 				return getActiveLocationListSuccess;
			case OIDConstant.GET_ACTIVE_LOCATION_LIST_FAILURE_OID: 				return getActiveLocationListFailure;
			case OIDConstant.CREATE_PLAN_SUCCESS_OID: 							return createPlanSuccess;
			case OIDConstant.CREATE_PLAN_FAILURE_OID: 							return createPlanFailure;
			case OIDConstant.UPDATE_PLAN_SUCCESS_OID: 							return updatePlanSuccess;
			case OIDConstant.UPDATE_PLAN_FAILURE_OID: 							return updatePlanFailure;
			case OIDConstant.DELETE_PLAN_SUCCESS_OID: 							return deletePlanSuccess;
			case OIDConstant.DELETE_PLAN_FAILURE_OID: 							return deletePlanFailure;
			case OIDConstant.GET_PLAN_LIST_SUCCESS_OID: 						return getPlanListSuccess;
			case OIDConstant.GET_PLAN_LIST_FAILURE_OID: 						return getPlanListFailure;
			case OIDConstant.SEARCH_PLAN_BY_NAME_SUCCESS_OID: 					return searchPlanByNameSuccess;
			case OIDConstant.SEARCH_PLAN_BY_NAME_FAILURE_OID: 					return searchPlanByNameFailure;
			case OIDConstant.GET_PLAN_BY_ID_SUCCESS_OID: 						return getPlanByIdSuccess;
			case OIDConstant.GET_PLAN_BY_ID_FAILURE_OID: 						return getPlanByIdFailure;
			case OIDConstant.CHANGE_STATUS_PLAN_SUCCESS_OID: 					return changeStatusPlanSuccess;
			case OIDConstant.CHANGE_STATUS_PLAN_FAILURE_OID: 					return changeStatusPlanFailure;
			case OIDConstant.GET_ACTIVE_PLAN_LIST_SUCCESS_OID: 					return getActivePlanListSuccess;
			case OIDConstant.GET_ACTIVE_PLAN_LIST_FAILURE_OID: 					return getActivePlanListFailure;
			case OIDConstant.GET_VALID_PLAN_LIST_SUCCESS_OID: 					return getValidPlanListSuccess;
			case OIDConstant.GET_VALID_PLAN_LIST_FAILURE_OID: 					return getValidPlanListFailure;
			case OIDConstant.CREATE_VOUCHER_PROFILE_SUCCESS_OID: 				return createVoucherProfileSuccess;
			case OIDConstant.CREATE_VOUCHER_PROFILE_FAILURE_OID: 				return createVoucherProfileFailure;
			case OIDConstant.UPDATE_VOUCHER_PROFILE_SUCCESS_OID: 				return updateVoucherProfileSuccess;
			case OIDConstant.UPDATE_VOUCHER_PROFILE_FAILURE_OID: 				return updateVoucherProfileFailure;
			case OIDConstant.DELETE_VOUCHER_PROFILE_SUCCESS_OID: 				return deleteVoucherProfileSuccess;
			case OIDConstant.DELETE_VOUCHER_PROFILE_FAILURE_OID: 				return deleteVoucherProfileFailure;
			case OIDConstant.GET_VOUCHER_PROFILE_LIST_SUCCESS_OID: 				return getVoucherProfileListSuccess;
			case OIDConstant.GET_VOUCHER_PROFILE_LIST_FAILURE_OID: 				return getVoucherProfileListFailure;
			case OIDConstant.SEARCH_VOUCHER_PROFILE_BY_NAME_SUCCESS_OID: 		return searchVoucherProfileByNameSuccess;
			case OIDConstant.SEARCH_VOUCHER_PROFILE_BY_NAME_FAILURE_OID: 		return searchVoucherProfileByNameFailure;
			case OIDConstant.GET_VOUCHER_PROFILE_BY_ID_SUCCESS_OID: 			return getVoucherProfileByIdSuccess;
			case OIDConstant.GET_VOUCHER_PROFILE_BY_ID_FAILURE_OID: 			return getVoucherProfileByIdFailure;
			case OIDConstant.CHANGE_STATUS_VOUCHER_PROFILE_SUCCESS_OID: 		return changeStatusVoucherProfileSuccess;
			case OIDConstant.CHANGE_STATUS_VOUCHER_PROFILE_FAILURE_OID: 		return changeStatusVoucherProfileFailure;
			case OIDConstant.CREATE_CONCURRENT_POLICY_SUCCESS_OID: 				return createConcurrentPolicySuccess;
			case OIDConstant.CREATE_CONCURRENT_POLICY_FAILURE_OID: 				return createConcurrentPolicyFailure;
			case OIDConstant.UPDATE_CONCURRENT_POLICY_SUCCESS_OID: 				return updateConcurrentPolicySuccess;
			case OIDConstant.UPDATE_CONCURRENT_POLICY_FAILURE_OID: 				return updateConcurrentPolicyFailure;
			case OIDConstant.DELETE_CONCURRENT_POLICY_SUCCESS_OID: 				return deleteConcurrentPolicySuccess;
			case OIDConstant.DELETE_CONCURRENT_POLICY_FAILURE_OID: 				return deleteConcurrentPolicyFailure;
			case OIDConstant.GET_CONCURRENT_POLICY_LIST_SUCCESS_OID: 			return getConcurrentPolicyListSuccess;
			case OIDConstant.GET_CONCURRENT_POLICY_LIST_FAILURE_OID: 			return getConcurrentPolicyListFailure;
			case OIDConstant.SEARCH_CONCURRENT_POLICY_BY_NAME_SUCCESS_OID: 		return searchConcurrentPolicyByNameSuccess;
			case OIDConstant.SEARCH_CONCURRENT_POLICY_BY_NAME_FAILURE_OID: 		return searchConcurrentPolicyByNameFailure;
			case OIDConstant.GET_CONCURRENT_POLICY_BY_ID_SUCCESS_OID: 			return getConcurrentPolicyByIdSuccess;
			case OIDConstant.GET_CONCURRENT_POLICY_BY_ID_FAILURE_OID: 			return getConcurrentPolicyByIdFailure;
			case OIDConstant.CHANGE_STATUS_CONCURRENT_POLICY_SUCCESS_OID: 		return changeStatusConcurrentPolicySuccess;
			case OIDConstant.CHANGE_STATUS_CONCURRENT_POLICY_FAILURE_OID: 		return changeStatusConcurrentPolicyFailure;
			case OIDConstant.GET_ACTIVE_CONCURRENT_POLICY_LIST_SUCCESS_OID: 	return getActiveConcurrentPolicyListSuccess;
			case OIDConstant.GET_ACTIVE_CONCURRENT_POLICY_LIST_FAILURE_OID: 	return getActiveConcurrentPolicyListFailure;
			case OIDConstant.CREATE_CUSTOMER_SUCCESS_OID: 						return createCustomerSuccess;
			case OIDConstant.CREATE_CUSTOMER_FAILURE_OID: 						return createCustomerFailure;
			case OIDConstant.UPDATE_CUSTOMER_SUCCESS_OID: 						return updateCustomerSuccess;
			case OIDConstant.UPDATE_CUSTOMER_FAILURE_OID: 						return updateCustomerFailure;
			case OIDConstant.DELETE_CUSTOMER_SUCCESS_OID: 						return deleteCustomerSuccess;
			case OIDConstant.DELETE_CUSTOMER_FAILURE_OID: 						return deleteCustomerFailure;
			case OIDConstant.GET_CUSTOMER_LIST_SUCCESS_OID: 					return getCustomerListSuccess;
			case OIDConstant.GET_CUSTOMER_LIST_FAILURE_OID: 					return getCustomerListFailure;
			case OIDConstant.SEARCH_CUSTOMER_BY_NAME_SUCCESS_OID: 				return searchCustomerByNameSuccess;
			case OIDConstant.SEARCH_CUSTOMER_BY_NAME_FAILURE_OID: 				return searchCustomerByNameFailure;
			case OIDConstant.GET_CUSTOMER_BY_ID_SUCCESS_OID: 					return getCustomerByIdSuccess;
			case OIDConstant.GET_CUSTOMER_BY_ID_FAILURE_OID: 					return getCustomerByIdFailure;
			case OIDConstant.CHANGE_STATUS_CUSTOMER_SUCCESS_OID: 				return changeStatusCustomerSuccess;
			case OIDConstant.CHANGE_STATUS_CUSTOMER_FAILURE_OID: 				return changeStatusCustomerFailure;
			case OIDConstant.GET_CUSTOMER_LIST_BY_CID_MAC_SUCCESS_OID: 			return getCustomerByCidMacSuccess;
			case OIDConstant.GET_CUSTOMER_LIST_BY_CID_MAC_FAILURE_OID: 			return getCustomerByCidMacFailure;
			case OIDConstant.RECHARGE_CUSTOMER_QUOTA_SUCCESS_OID: 				return rechargeCustomerQuotaSuccess;
			case OIDConstant.RECHARGE_CUSTOMER_QUOTA_FAILURE_OID: 				return rechargeCustomerQuotaFailure;
			case OIDConstant.CHANGE_CUSTOMER_PASSWORD_SUCCESS_OID: 				return changeCustomerPasswordSuccess;
			case OIDConstant.CHANGE_CUSTOMER_PASSWORD_FAILURE_OID: 				return changeCustomerPasswordFailure;
			case OIDConstant.GET_CUSTOMER_PLAN_DETAILS_SUCCESS_OID: 			return getCustomerPlanDetailsSuccess;
			case OIDConstant.GET_CUSTOMER_PLAN_DETAILS_FAILURE_OID: 			return getCustomerPlanDetailsFailure;
			case OIDConstant.EXPORT_CUSTOMER_LIST_SUCCESS_OID: 					return exportCustomerListSuccess;
			case OIDConstant.EXPORT_CUSTOMER_LIST_FAILURE_OID: 					return exportCustomerListFailure;
			case OIDConstant.LOGIN_CUSTOMER_SUCCESS_OID: 						return loginCustomerSuccess;
			case OIDConstant.LOGIN_CUSTOMER_FAILURE_OID: 						return loginCustomerFailure;
			case OIDConstant.LOGOUT_CUSTOMER_SUCCESS_OID: 						return logoutCustomerSuccess;
			case OIDConstant.LOGOUT_CUSTOMER_FAILURE_OID: 						return logoutCustomerFailure;
			case OIDConstant.RENEW_CUSTOMER_SUCCESS_OID: 						return renewCustomerSuccess;
			case OIDConstant.RENEW_CUSTOMER_FAILURE_OID: 						return renewCustomerFailure;
			case OIDConstant.GET_TIMEBASE_POLICY_LIST_SUCCESS_OID: 				return getTimeBasePolicyListSuccess;
			case OIDConstant.GET_TIMEBASE_POLICY_LIST_FAILURE_OID: 				return getTimeBasePolicyListFailure;
			case OIDConstant.CREATE_TIMEBASE_POLICY_LIST_SUCCESS_OID: 			return createTimeBasePolicySuccess;
			case OIDConstant.CREATE_TIMEBASE_POLICY_LIST_FAILURE_OID: 			return createTimeBasePolicyFailure;
			case OIDConstant.DELETE_TIMEBASE_POLICY_LIST_SUCCESS_OID: 			return deleteTimeBasePolicySuccess;
			case OIDConstant.DELETE_TIMEBASE_POLICY_LIST_FAILURE_OID: 			return deleteTimeBasePolicyFailure;
			case OIDConstant.UPDATE_TIMEBASE_POLICY_LIST_SUCCESS_OID: 			return updateTimeBasePolicySuccess;
			case OIDConstant.UPDATE_TIMEBASE_POLICY_LIST_FAILURE_OID: 			return updateTimeBasePolicyFailure;
			case OIDConstant.CHANGE_STATUS_TIMEBASE_POLICY_LIST_SUCCESS_OID: 	return changeStatusTimeBasePolicySuccess;
			case OIDConstant.CHANGE_STATUS_TIMEBASE_POLICY_LIST_FAILURE_OID: 	return changeStatusTimeBasePolicyFailure;
			case OIDConstant.FIND_ALL_TIMEBASE_POLICY_LIST_SUCCESS_OID: 		return findAllTimeBasePolicySuccess;
			case OIDConstant.FIND_ALL_TIMEBASE_POLICY_LIST_FAILURE_OID: 		return findAllTimeBasePolicyFailure;
			case OIDConstant.GET_TIMEBASE_POLICY_BY_ID_SUCCESS_OID: 			return findByIdTimeBasePolicySuccess;
			case OIDConstant.GET_TIMEBASE_POLICY_BY_ID_FAILURE_OID: 			return findByIdTimeBasePolicyFailure;
			case OIDConstant.FIND_ALL_ACTIVE_TIMEBASE_POLICY_LIST_SUCCESS_OID: 	return findAllActiveTimeBasePolicySuccess;
			case OIDConstant.FIND_ALL_ACTIVE_TIMEBASE_POLICY_LIST_FAILURE_OID: 	return findAllActiveTimeBasePolicyFailure;
			case OIDConstant.GET_OTP_MANAGEMENT_LIST_SUCCESS_OID: 				return getOtpManagmentListSuccess;
			case OIDConstant.GET_OTP_MANAGEMENT_LIST_FAILURE_OID: 				return getOtpManagmentListFailure;
			case OIDConstant.CREATE_OTP_MANAGEMENT_SUCCESS_OID: 				return createOtpManagmentSuccess;
			case OIDConstant.CREATE_OTP_MANAGEMENT_FAILURE_OID: 				return createOtpManagmentFailure;
			case OIDConstant.UPDATE_OTP_MANAGEMENT_FAILURE_OID: 				return updateOtpManagmentFailure;
			case OIDConstant.UPDATE_OTP_MANAGEMENT_SUCCESS_OID: 				return updateOtpManagmentSuccess;
			case OIDConstant.DELETE_OTP_MANAGEMENT_SUCCESS_OID: 				return deleteOtpManagmentSuccess;
			case OIDConstant.DELETE_OTP_MANAGEMENT_FAILURE_OID: 				return deleteOtpManagmentFailure;
			case OIDConstant.GET_PROFILEID_OTP_MANAGEMENT_SUCCESS_OID: 			return findByProfileIdOtpManagmentSuccess;
			case OIDConstant.GET_PROFILEID_OTP_MANAGEMENT_FAILURE_OID: 			return findByProfileIdOtpManagmentFailure;
			case OIDConstant.GET_PROFILENAME_OTP_MANAGEMENT_SUCCESS_OID: 		return findByProfileNameOtpManagmentSuccess;
			case OIDConstant.GET_PROFILENAME_OTP_MANAGEMENT_FAILURE_OID: 		return findByProfileNameOtpManagmentFailure;
			case OIDConstant.FIND_ALL_PARTNER_LIST_SUCCESS_OID: 				return findAllPartnerListSuccess;
			case OIDConstant.FIND_ALL_PARTNER_LIST_FAILURE_OID: 				return findAllPartnerListFailure;
			case OIDConstant.CREATE_PARTNER_LIST_SUCCESS_OID: 					return createPartnerListSuccess;
			case OIDConstant.CREATE_PARTNER_LIST_FAILURE_OID: 					return createPartnerListFailure;
			case OIDConstant.DELETE_PARTNER_LIST_SUCCESS_OID: 					return deletePartnerListSuccess;
			case OIDConstant.DELETE_PARTNER_LIST_FAILURE_OID: 					return deletePartnerListFailure;
			case OIDConstant.UPDATE_PARTNER_LIST_SUCCESS_OID: 					return updatePartnerListSuccess;
			case OIDConstant.UPDATE_PARTNER_LIST_FAILURE_OID: 					return updatePartnerListFailure;
			case OIDConstant.FIND_PARTNER_BY_ID_SUCCESS_OID: 					return findPartnerByIdSuccess;
			case OIDConstant.FIND_PARTNER_BY_ID_FAILURE_OID: 					return findPartnerByIdFailure;
			case OIDConstant.FIND_DISTRIBUTER_OF_PARTNER_LIST_SUCCESS_OID: 		return findDistributeOfPartnerListSuccess;
			case OIDConstant.FIND_DISTRIBUTER_OF_PARTNER_LIST_FAILURE_OID:		return findDistributeOfPartnerListFailure;
			case OIDConstant.FIND_ALL_TEMPLATE_LIST_SUCCESS_OID:                return findAllTemplateListSuccess;
			case OIDConstant.FIND_ALL_TEMPLATE_LIST_FAILURE_OID:                return findAllTemplateListFailure;
			case OIDConstant.DELETE_TEMPLATE_SUCCESS_OID:                       return deleteTemplateSuccess;
			case OIDConstant.DELETE_TEMPLATE_FAILURE_OID:                       return deleteTemplateFailure;
			case OIDConstant.CREATE_TEMPLATE_SUCCESS_OID:                       return createTemplateSuccess;
			case OIDConstant.CREATE_TEMPLATE_FAILURE_OID:                       return createTemplateFailure;
			case OIDConstant.UPDATE_TEMPLATE_SUCCESS_OID:                       return updateTemplateSuccess;
			case OIDConstant.UPDATE_TEMPLATE_FAILURE_OID:                       return updateTemplateFailure;
			case OIDConstant.SEARCH_TEMPLATE_SUCCESS_OID:                       return searchTemplateSuccess;
			case OIDConstant.SEARCH_TEMPLATE_FAILURE_OID:                       return searchTemplateFailure;

			case OIDConstant.GET_ALL_RESELLER_LIST_SUCCESS_OID:					return getAllResellerListSuccess;
			case OIDConstant.GET_ALL_RESELLER_LIST_FAILURE_OID:					return getAllResellerListFailure;
			case OIDConstant.FIND_ALL_RESELLER_LIST_SUCCESS_OID:				return findAllResellerListSuccess;
			case OIDConstant.FIND_ALL_RESELLER_LIST_FAILURE_OID:				return findAllResellerListFailure;
			case OIDConstant.FIND_RESELLER_BY_ID_SUCCESS_OID:					return findResellerByIdSuccess;
			case OIDConstant.FIND_RESELLER_BY_ID_FAILURE_OID:					return findResellerByIdFailure;
			case OIDConstant.CREATE_RESELLER_SUCCESS_OID:						return createResellerSuccess;
			case OIDConstant.CREATE_RESELLER_FAILURE_OID:						return createResellerFailure;
			case OIDConstant.CREATE_MANAGE_BALANCE_FOR_RESELLER_SUCCESS_OID:	return createManageBalanceForResellerSuccess;
			case OIDConstant.CREATE_MANAGE_BALANCE_FOR_RESELLER_FAILURE_OID:	return createManageBalanceForResellerFailure;
			case OIDConstant.CREATE_ADD_BALANCE_FOR_RESELLER_SUCCESS_OID:		return createAddBalanceForResellerSuccess;
			case OIDConstant.CREATE_ADD_BALANCE_FOR_RESELLER_FAILURE_OID:		return createAddBalanceForResellerFailure;
			case OIDConstant.UPDATE_RESELLER_SUCCESS_OID:						return updateResellerSuccess;
			case OIDConstant.UPDATE_RESELLER_FAILURE_OID:						return updateResellerFailure;
			case OIDConstant.DELETE_RESELLER_SUCCESS_OID:						return deleteResellerSuccess;
			case OIDConstant.DELETE_RESELLER_FAILURE_OID:						return deleteResellerFailure;
			case OIDConstant.SEARCH_RESELLERS_LIST_SUCCESS_OID:					return searchResellerListSuccess;
			case OIDConstant.SEARCH_RESELLERS_LIST_FAILURE_OID:					return searchResellerListFailure;
			case OIDConstant.VALID_LOGIN_USER_FOR_RESELLER_SUCCESS_OID:			return validLoginUserForResellerSuccess;
			case OIDConstant.VALID_LOGIN_USER_FOR_RESELLER_FAILURE_OID:			return validLoginUserForResellerFailure;
			case OIDConstant.CHANGE_RESELLER_PASSWORD_SUCCESS_OID:				return changeResellerPasswordSuccess;
			case OIDConstant.CHANGE_RESELLER_PASSWORD_FAILURE_OID:				return changeResellerPasswordFailure;
			case OIDConstant.SEARCH_RESELLER_BY_LOCATION_ID_LIST_SUCCESS_OID:	return searchResellerByLocationIdListSuccess;
			case OIDConstant.SEARCH_RESELLER_BY_LOCATION_ID_LIST_FAILURE_OID:	return searchResellerByLocationIdListFailure;
			case OIDConstant.CHANGE_RESELLER_STATUS_SUCCESS_OID:				return changeResellerStatusSuccess;
			case OIDConstant.CHANGE_RESELLER_STATUS_FAILURE_OID:				return changeResellerStatusFailure;
			case OIDConstant.VALIDATE_VOUCHER_SUCCESS_OID:						return validateVoucherSuccess;
			case OIDConstant.VALIDATE_VOUCHER_FAILURE_OID:						return validateVoucherFailure;
			case OIDConstant.GET_ALL_VOUCHERS_LIST_SUCCESS_OID:					return getAllVouchersListSuccess;
			case OIDConstant.GET_ALL_VOUCHERS_LIST_FAILURE_OID:					return getAllVouchersListFailure;
			case OIDConstant.FIND_VOUCHERS_BY_BATCH_ID_SUCCESS_OID:				return findVouchersByBatchIdSuccess;
			case OIDConstant.FIND_VOUCHERS_BY_BATCH_ID_FAILURE_OID:				return findVouchersByBatchIdFailure;
			case OIDConstant.FIND_VOUCHERS_SUCCESS_OID:							return findVouchersSuccess;
			case OIDConstant.FIND_VOUCHERS_FAILURE_OID:							return findVouchersFailure;
			case OIDConstant.CREATE_VOUCHER_ID_SUCCESS_OID:						return createVoucherIdSuccess;
			case OIDConstant.CREATE_VOUCHER_ID_FAILURE_OID:						return createVoucherIdFailure;
			case OIDConstant.CHANGE_VOUCHER_STATUS_TO_ACTIVE_SUCCESS_OID:		return changeVoucherStatusToActiveSuccess;
			case OIDConstant.CHANGE_VOUCHER_STATUS_TO_ACTIVE_FAILURE_OID:		return changeVoucherStatusToActiveFailure;
			case OIDConstant.CHANGE_VOUCHER_STATUS_TO_BLOCK_SUCCESS_OID:		return changeVoucherStatusToBlockSuccess;
			case OIDConstant.CHANGE_VOUCHER_STATUS_TO_BLOCK_FAILURE_OID:		return changeVoucherStatusToBlockFailure;
			case OIDConstant.CHANGE_VOUCHER_STATUS_TO_UNBLOCK_SUCCESS_OID:		return changeVoucherStatusToUnblockSuccess;
			case OIDConstant.CHANGE_VOUCHER_STATUS_TO_UNBLOCK_FAILURE_OID:		return changeVoucherStatusToUnblockFailure;
			case OIDConstant.CHANGE_VOUCHER_STATUS_TO_SCRAP_SUCCESS_OID:		return changeVoucherStatusToScrapSuccess;
			case OIDConstant.CHANGE_VOUCHER_STATUS_TO_SCRAP_FAILURE_OID:		return changeVoucherStatusToScrapFailure;
			case OIDConstant.SEND_SMS_FOR_VOUCHER_SUCCESS_OID:					return sendSMSForVucherSuccess;
			case OIDConstant.SEND_SMS_FOR_VOUCHER_FAILURE_OID:					return sendSMSForVucherFailure;
			case OIDConstant.CREATE_VOUCHERBATCH_SUCCESS_OID:                   return createVoucherBatchSuccess;
			case OIDConstant.CREATE_VOUCHERBATCH_FAILURE_OID:                   return createVoucherBatchFailure;
			case OIDConstant.UPDATE_VOUCHERBATCH_SUCCESS_OID:                   return updateVoucherBatchSuccess;
			case OIDConstant.UPDATE_VOUCHERBATCH_FAILURE_OID:                   return updateVoucherBatchFailure;
			case OIDConstant.DELETE_VOUCHERBATCH_SUCCESS_OID:                   return deleteVoucherBatchSuccess;
			case OIDConstant.DELETE_VOUCHERBATCH_FAILURE_OID:                   return deleteVoucherBatchFailure;
			case OIDConstant.FINDALL_VOUCHERBATCH_SUCCESS_OID:                  return findAllVoucherBatchSuccess;
			case OIDConstant.FINDALL_VOUCHERBATCH_FAILURE_OID:                  return findAllVoucherBatchFailure;
			case OIDConstant.GET_VOUCHERBATCH_LIST_SUCCESS_OID:                 return getVoucherBatchListSuccess;
			case OIDConstant.GET_VOUCHERBATCH_LIST_FAILURE_OID:                 return getVoucherBatchListFailure;
			case OIDConstant.FIND_VOUCHERBATCH_BY_ID_SUCCESS_OID:               return findVoucherBatchByIdSuccess;
			case OIDConstant.FIND_VOUCHERBATCH_BY_ID_FAILURE_OID:               return findVoucherBatchByIdFailure;
			case OIDConstant.FIND_VOUCHERBATCH_WITHOUT_RESELLER_SUCCESS_OID:    return  findVoucherBatchWithoutResellerSuccess;
			case OIDConstant.FIND_VOUCHERBATCH_WITHOUT_RESELLER_FAILURE_OID:    return  findVoucherBatchWithoutResellerFailure;
			case OIDConstant.SEARCH_VOUCHERBATCH_BY_DATE_SUCCESS_OID:           return   searchVoucherBatchByDateSuccess;
			case OIDConstant.SEARCH_VOUCHERBATCH_BY_DATE_FAILURE_OID:           return   searchVoucherBatchByDateFailure;
			case OIDConstant.ASSIGN_RESELLER_TO_VOUCHERBATCH_SUCCESS_OID:       return   assignResellertoVoucherBatchSuccess;
			case OIDConstant.ASSIGN_RESELLER_TO_VOUCHERBATCH_FAILURE_OID:       return   assignResellertoVoucherBatchFailure;
			case OIDConstant.FINDALL_VOUCHERS_SUCCESS_OID:       				return   findAllVoucherSuccess;
			case OIDConstant.FINDALL_VOUCHERS_FAILURE_OID:        				return   findAllVoucherFailure;
			case OIDConstant.GENERATE_BATCH_AND_VOUCHERS_SUCCESS_OID:       	return   generateBatchandVoucherSuccess;
			case OIDConstant.GENERATE_BATCH_AND_VOUCHERS_FAILURE_OID:       	return   generateBatchandVoucherFailure;
			default:
				System.out.println("Unknown SNMP OID");
				return null;
		}
	}
}
