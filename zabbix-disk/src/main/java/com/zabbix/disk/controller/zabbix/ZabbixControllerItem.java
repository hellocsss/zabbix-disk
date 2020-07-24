package com.zabbix.disk.controller.zabbix;

import com.zabbix.disk.entity.ChartBase;
import com.zabbix.disk.entity.page.Item;
import com.zabbix.disk.service.DiskService;
import com.zabbix.disk.service.ZabbixServiceItem;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("GetAllHostItems")
public class ZabbixControllerItem {
    @Resource
    private DiskService diskService;
    @Resource
    private ZabbixServiceItem zabbixServiceItem;



    @RequestMapping("/GetAllHostItem")
    public String GetAllHostItem(Model model) throws Exception {
        Map<String, List<Item>> getAllHostAndItem=this.diskService.getAllDiskHostAndItem();
        model.addAttribute("HostAndItem",getAllHostAndItem);
      /*  return "/login2";*/
        return "/Host";

    }
    @RequestMapping("/GetHostAndItem")
    public String GetHostAndItem(String Hostname,Model model) throws Exception {
        String hostId=this.diskService.getHostByHostId(Hostname);
        Map<String, Item> itemMap=this.diskService.getAllDiskItemMapByHostId(hostId);
        List<String> list = zabbixServiceItem.splictString(itemMap);
        model.addAttribute("hostId",hostId);
        model.addAttribute("listitem",list);
        return "/HostItem";
    }
    @RequestMapping("/getDiskItem")
    public String getDiskItem4(String hostid,String item,Model model) throws Exception {
        Map<String, Item> itemMap=this.diskService.getAllDiskItemMapByHostId(hostid);
        itemMap.forEach((key, value) -> {
            String substring = key.substring(0, key.indexOf(":"));
            if(item.equals(substring)){
                Item item1= new Item();
                item1.setName(value.getName());
                item1.setType(value.getType());
                try {
                    List<ChartBase> history = this.diskService.getHistory(hostid, item1);
                    ChartBase chartBase = history.get(0);

                    if (value.getName().contains("Free")){
                        model.addAttribute("Free",chartBase.getValue()+"%");

                    }else if (value.getName().contains("Space")){
                        model.addAttribute("Space",chartBase.getValue()+"%");

                    }else if (value.getName().contains("Used")){
                        model.addAttribute("Used",zabbixServiceItem.getPrintSize(Long.parseLong(chartBase.getValue())));

                    }else if (value.getName().contains("Total")){
                        model.addAttribute("Total",zabbixServiceItem.getPrintSize(Long.parseLong(chartBase.getValue())));

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        model.addAttribute("item",item);
        return "/change";
    }


}
