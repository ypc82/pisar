import com.mongodb.Mongo;
import com.mongodb.DB; 
import com.mongodb.DBCollection; 
//import com.mongodb.BasicDBObject; 
//import com.mongodb.BasicDBObjectBuilder; 
//import com.mongodb.DBObject; 
//import com.mongodb.MongoClient;

import java.net.UnknownHostException; 
import java.io.*;

public class Save_ImgTag{ 
	public static void main(String args[]){ 
		String input;
		String[] data;
		String filename = "tbl_imagetag.txt";
	//	String filename = "image_tag.txt"; 
	//	String[] tag_num; 
	//	String[] tag = new String[170];
	//	int[] num = new int[170] ;
		int index1, index2, end;

		int IT_ID; 
		String IT_FlickrID; 
		String IT_LastUpdate; 
		String[] IT_TagList; 
		String[] TagName; 
		int[] TagNum;
		
		try{ 
			MongoClient mongoClient = new MongoClient(); 
			DB db = mongoClient.getDB("pisar"); 
			DBCollection ImageTag = db.getCollection("ImageTag");

			try{
				File file = new File(filename);	
				FileReader fileReader = new FileReader(file);
				BufferedReader buff = new BufferedReader(fileReader);

				while(buff.ready()){
					input = buff.readLine();
					input = input.replace(".","");
					data = input.replace("\"","").split(",");
					
					IT_ID = Integer.parseInt(data[0]);
					IT_FlickrID = data[1];
					IT_LastUpdate = data[3];

					data[2] = data[2].substring(2);
					IT_TagList = data[2].split("==");
					for(int i=0; i<IT_TagList.length;i++){
						index1 = IT_TagList[i].indexOf('[');
						index2 = 1+IT_TagList[i].indexOf('(');
						end  = IT_TagList[i].indexOf(')');
						
						TagName[i] = IT_TagList[i].substring(0,index1);
						TagNum[i] = Integer.parseInt(IT_TagList[i].substring(index2,end));
					}

					//store in MongoDB
					BasicDBObject image = new BasicDBObject();
					image.put("IT_ID",IT_ID);
					image.put("IT_FlickrID",IT_FlickrID);
					image.put("IT_LastUpdate",IT_LastUpdate);
					
				/*	BasicDBObject tags = new BasicDBObject();
					for(i=0; i<IT_TagList.length; i++){
						tags.put("TagName",TagName[i]);
						tags.put("TagNum",TagNum[i]);
					}
					image.put("TagList",tags);*/

					ArrayList<BasicDBObject> tags = new ArrayList<BasicDBObject>();
					for(i=0; i<IT_TagList.length; i++){
						tags.add(BasicDBObjectBuilder.start()
						         .add("TagName",TagName[i]).add("TagNum",TagNum[i]).get());
					}

					image.put("TagList",tags);
				}

				buff.close();
				fileReader.close();
			}catch(IOException ex){
				ex.printStackTrace();
				System.exit(1);
			}

		}catch(UnknownHostException except){
			System.out.println("Error!");
		}
	}
}
