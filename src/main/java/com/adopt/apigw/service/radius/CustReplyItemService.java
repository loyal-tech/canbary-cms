package com.adopt.apigw.service.radius;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.mapper.postpaid.CustReplyItemMapper;
import com.adopt.apigw.model.radius.CustReplyItem;
import com.adopt.apigw.pojo.api.ClientsPojo;
import com.adopt.apigw.pojo.api.CustReplyItemPojo;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.CustomerReplyMessage;
import com.adopt.apigw.repository.radius.CustReplyItemRepo;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustReplyItemService extends AbstractService<CustReplyItem, CustReplyItemPojo, Integer> {

    @Autowired
    private CustReplyItemRepo custReplyItemRepo;
    @Autowired
    private CustReplyItemMapper custReplyItemMapper;
    @Autowired
    private MessageSender messageSender;

    @Override
    protected JpaRepository<CustReplyItem, Integer> getRepository() {
        return custReplyItemRepo;
    }
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    public List<CustReplyItem> getLisByCustIdt(Integer custId) {
        return custReplyItemRepo.findBycustid(custId);
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Cust Reply Item");
        List<CustReplyItemPojo> custReplyItemPojos = getRepository().findAll().stream()
                .map(data -> custReplyItemMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createExcel(workbook, sheet, CustReplyItemPojo.class, custReplyItemPojos, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<CustReplyItemPojo> custReplyItemPojos = getRepository().findAll().stream()
                .map(data -> custReplyItemMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createPDF(doc, CustReplyItemPojo.class, custReplyItemPojos, null);
    }

    @Override
    public CustReplyItem save(CustReplyItem entity) {
        CustReplyItem save =  getRepository().save(entity);
        CustomerReplyMessage message = new CustomerReplyMessage(save);
        kafkaMessageSender.send(new KafkaMessageData(message, CustomerReplyMessage.class.getSimpleName()));
//        messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_CUST_REPLY);
        return save;
    }

    @Override
    public CustReplyItem update(CustReplyItem entity) {
        CustReplyItem update =  getRepository().save(entity);
        CustomerReplyMessage message = new CustomerReplyMessage(update);
        kafkaMessageSender.send(new KafkaMessageData(message, CustomerReplyMessage.class.getSimpleName()));
//        messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_CUST_REPLY);
        return update;
    }

//    @Override
//    public void delete(Long id) {
//
//    }

}
