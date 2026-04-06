package com.adopt.apigw.controller.common.dropdown;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.adopt.apigw.controller.base.BaseController;

import java.util.List;

//@RestController
//@RequestMapping("/commonList")
public class CommonListController
{
  //      extends BaseController<CommonList> {

//    @Autowired
//    private CommonListService commonListService;
//
//    @GetMapping("/{type}/getAll")
//    public ResponseEntity<List<CommonList>> getCommonListByType(@PathVariable String type){
//        return new ResponseEntity<>(commonListService.getCommonListByType(type), HttpStatus.OK);
//    }
//
//    @PostMapping("/save")
//    public ResponseEntity<CommonList> addCommonList(@RequestBody CommonList commonList){
//        return new ResponseEntity<>(commonListService.saveCommonType(commonList), HttpStatus.OK);
//    }
//
//    @PostMapping("/delete/{id}")
//    public ResponseEntity<CommonList> deleteCommonList(@PathVariable Integer id){
//        CommonList list = commonListService.getCommonListById(id);
//        if(list!=null){
//            commonListService.deleteCommonTypeById(list);
//            return new ResponseEntity<>(list, HttpStatus.OK);
//        }
//        else{
//            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
//        }
//    }
}
