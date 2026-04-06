package com.adopt.apigw.modules.ippool.service;

import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustChargeDetails;
import com.adopt.apigw.modules.ippool.domain.IPPool;
import com.adopt.apigw.modules.ippool.domain.IPPoolDtls;
import com.adopt.apigw.modules.ippool.mapper.IPPoolDtlsMapper;
import com.adopt.apigw.modules.ippool.model.IPPoolDtlsDTO;
import com.adopt.apigw.modules.ippool.model.IpAddressFindResDTO;
import com.adopt.apigw.modules.ippool.repository.IPPoolDtlsRepository;
import com.adopt.apigw.modules.ippool.repository.IPPoolRepository;
import com.adopt.apigw.modules.ippool.utils.IpConfigConstant;
import com.adopt.apigw.repository.postpaid.CustChargeRepository;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.utils.CommonConstants;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IPPoolDtlsService extends ExBaseAbstractService<IPPoolDtlsDTO, IPPoolDtls, Long> {

    @Autowired
    private IPPoolDtlsRepository ipPoolDtlsRepository;

    @Autowired
    private IPPoolDtlsMapper ipPoolDtlsMapper;

    @Autowired
    private IPPoolRepository ipPoolRepository;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private CustChargeRepository custChargeRepository;
    private String ipBlockTime;

    public IPPoolDtlsService(IPPoolDtlsRepository repository, IPPoolDtlsMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[IPPoolDtlsService]";
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        return null;
    }

    public IpAddressFindResDTO findByIpAddress(String ipAddress) throws ParseException {

        IpAddressFindResDTO  ipAddressFindResDTO=new IpAddressFindResDTO();
        List<IPPoolDtls> ipPoolDtlsList= ipPoolDtlsRepository.findAllByIpAddress(ipAddress);
        if(ipPoolDtlsList!=null && ipPoolDtlsList.size()>0)
        {
            ipAddressFindResDTO.setIpAddress (ipPoolDtlsList.get(0).getIpAddress());
            ipAddressFindResDTO.setIpStatus(ipPoolDtlsList.get(0).getStatus());
            ipAddressFindResDTO.setPoolDetailsId(ipPoolDtlsList.get(0).getPoolDetailsId());
            IPPool ipPool=ipPoolRepository.getOne(ipPoolDtlsList.get(0).getPoolId());
            if(ipPool!=null){
                ipAddressFindResDTO.setPoolName(ipPool.getPoolName());
                ipAddressFindResDTO.setDisplayName(ipPool.getDisplayName());
                ipAddressFindResDTO.setPoolType(ipPool.getPoolType());
                ipAddressFindResDTO.setIsStaticIp(ipPool.getIsStaticIpPool());
            }
            if(ipPoolDtlsList.get(0).getAllocatedId()!=null){
                ipAddressFindResDTO.setIpAllocated(ipPoolDtlsList.get(0).getAllocatedId());
                CustChargeDetails custChargeDetails= custChargeRepository.findByPurchaseEntityIdAndChargetype(ipPoolDtlsList.get(0).getAllocatedId(),CommonConstants.CHARGE_TYPE_STATIC_IP);
                if(custChargeDetails!=null){
                    Customers customers=custChargeDetails.getCustomer();
                    if(customers!=null){
                        ipAddressFindResDTO.setCustomerName(customers.getFullName());
                    }
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    if(custChargeDetails.getStartdate()!=null){
                        ipAddressFindResDTO.setStartDate(formatter.format(custChargeDetails.getStartdate()));
                    }
                    if(custChargeDetails.getEnddate() !=null){
                        ipAddressFindResDTO.setEndDate(formatter.format(custChargeDetails.getEnddate()));
                    }
                }
            }
        }
        return ipAddressFindResDTO;
    }

    public IPPoolDtlsDTO updateIPAddress(IPPoolDtlsDTO ipPoolDtlsDTO)throws Exception
    {
        IPPoolDtls ipPoolDtls=new IPPoolDtls();
        IPPoolDtlsDTO ipPoolDtlsDTO1=new IPPoolDtlsDTO();
        ipPoolDtls.setPoolDetailsId(ipPoolDtlsDTO.getPoolDetailsId());
        ipPoolDtls.setPoolId(ipPoolDtlsDTO.getPoolId());
        ipPoolDtls.setIpAddress(ipPoolDtlsDTO.getIpAddress());
        ipPoolDtlsDTO.setStatus(ipPoolDtlsDTO.getStatus());
        ipPoolDtlsDTO.setAllocatedId(ipPoolDtlsDTO.getAllocatedId());
        ipPoolDtls.setUnblockTime(ipPoolDtlsDTO.getUnblockTime());
        ipPoolDtls.setBlockByCustId(ipPoolDtls.getBlockByCustId());
        ipPoolDtlsDTO1=  ipPoolDtlsMapper.domainToDTO(ipPoolDtls,new CycleAvoidingMappingContext());
        updateEntity(ipPoolDtlsDTO1);
        return ipPoolDtlsDTO1;
    }

    public List<IPPoolDtls> findByPoolId(Long poolId){
        return ipPoolDtlsRepository.findByPoolId(poolId);
    }
    public Page<IPPoolDtls> findNonAllocatedPoolId(Long poolId, Integer page, Integer size, String sortBy, Integer sortOrder){
        PageRequest pageRequest = null;
        if (null != sortOrder && sortOrder.equals(CommonConstants.SORT_ORDER_DESC))
            pageRequest = PageRequest.of(page, size, Sort.by(sortBy).descending());
        else
            pageRequest = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return ipPoolDtlsRepository.findAllByPoolIdAndStatus(poolId, IpConfigConstant.IP_STATUS_FREE,pageRequest);
    }

    public IPPoolDtlsDTO findByAllocatedIp(Long allocatedIp){
        return getMapper().domainToDTO(ipPoolDtlsRepository.findByAllocatedIdAndStatus(allocatedIp,IpConfigConstant.IP_STATUS_ALLOCATED),new CycleAvoidingMappingContext());
    }

    public IPPoolDtlsDTO blockIp(IPPoolDtlsDTO requestDTO,Long custId) throws Exception {
        ipBlockTime = clientServiceSrv.getClientSrvByName(ClientServiceConstant.IP_BLOCK_TIME).get(0).getValue();
        if(!requestDTO.getStatus().equalsIgnoreCase(IpConfigConstant.IP_STATUS_BLOCK) && !requestDTO.getStatus().equalsIgnoreCase(IpConfigConstant.IP_STATUS_ALLOCATED)){
            requestDTO.setStatus(IpConfigConstant.IP_STATUS_BLOCK);
            requestDTO.setUnblockTime(LocalDateTime.now().plusMinutes(Integer.parseInt(ipBlockTime)));
            requestDTO.setBlockByCustId(custId);
            requestDTO = this.saveEntity(requestDTO);
            return requestDTO;
        }
        else{
            throw new RuntimeException("IP Already in block or allocated");
        }
    }

    public List<IPPoolDtlsDTO> findBlockedIp(LocalDateTime unblockTime){
        return ipPoolDtlsRepository.findAllByStatusAndUnblockTimeLessThanEqual(IpConfigConstant.IP_STATUS_BLOCK,unblockTime).stream().map(data->getMapper().domainToDTO(data,new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }


    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("IPPoolDtls");
        createExcel(workbook, sheet, IPPoolDtlsDTO.class, null,mvnoId);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        createPDF(doc, IPPoolDtlsDTO.class, null,mvnoId);
    }
}
