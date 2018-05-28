package com.yizhigou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.yizhigou.pojo.TbGoods;
import com.yizhigou.pojo.TbItem;
import com.yizhigou.pojogroup.Goods;
import com.yizhigou.sellergoods.service.GoodsService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Arrays;
import java.util.List;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Destination queueSolrDestination;//消息名称
	@Autowired
	private Destination queueSolrDeleteDestination;//删除索引
	@Autowired
	private Destination topicPageDeleteDestination;//删除静态页面

	@Autowired
	private Destination topicPageDestination;//生产静态页面

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	

	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			System.out.println("删除索引成功");
			//searchService.deleteByGoodsIds(Arrays.asList(ids));
			jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
			// 删除静态页面 发送消息
			jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}

//	@Reference
//	private SearchService searchService;

	//审核商品
	@RequestMapping("/updateStatus")
	public Result  updateStatus(Long[] id,String status){
		try {
			goodsService.updateStatus(id,status);

			System.out.println("id:"+id[0]+"--------status:"+status);
			//判断，status是1的状态，我再去调用接口方法导入数据
            //0未审核  1 审核通过 2 审核未通过 3 关闭
            if(status.equals("1")){
                List<TbItem> itemList=goodsService.findItemListByGoodsIdandStatus(id,status);
                //调用搜索接口，导入数据
                if(itemList.size()>0){
                    //searchService.importList(itemList);
                    System.out.println("索引库添加==同步成功");
                    //发送消息  1 发送消息的内容  2 类型转换
					String jsonStr= JSON.toJSONString(itemList);
					jmsTemplate.send(queueSolrDestination, new MessageCreator() {
						//消息发送
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(jsonStr);
						}
					});
                }else{
                    System.out.println("没有查询的数据");
                }
                for(Long goodsId:id){
                	//生成静态页面
					//itemPageService.genItemHtml(goodsId);
					jmsTemplate.send(topicPageDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(goodsId+"");
						}
					});
				}
            }
			return new Result(true,"修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"修改失败");
		}
	}
/*@Reference
private ItemPageService itemPageService;
	@RequestMapping("/genHtml")
	public void genHtml(Long goodsId){
		itemPageService.genItemHtml(goodsId);
	}*/
	
}
