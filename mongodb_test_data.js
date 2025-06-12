// MongoDB 测试数据脚本
// 使用此脚本在MongoDB中插入店铺地理位置数据
// 在MongoDB Shell中执行此脚本：mongosh < mongodb_test_data.js

// 连接数据库（假设使用默认连接）
// 切换到数据库
db = db.getSiblingDB('petshop_mongo_db');

// 删除现有的stores_location集合（如果存在）
db.stores_location.drop();

// 创建地理空间索引
db.stores_location.createIndex({ "location": "2dsphere" });

// 插入测试数据 - 北京地区的宠物店
db.stores_location.insertMany([
  {
    "storeId": NumberLong(1),
    "name": "汪汪之家 (西直门店)",
    "location": {
      "type": "Point",
      "coordinates": [116.3564, 39.9390] // [经度, 纬度] 西直门附近
    }
  },
  {
    "storeId": NumberLong(2), 
    "name": "喵星人俱乐部 (中关村店)",
    "location": {
      "type": "Point",
      "coordinates": [116.3085, 39.9599] // [经度, 纬度] 中关村附近
    }
  },
  {
    "storeId": NumberLong(3),
    "name": "宠物总动员 (国贸店)", 
    "location": {
      "type": "Point",
      "coordinates": [116.4574, 39.9185] // [经度, 纬度] 国贸附近
    }
  },
  // 增加更多测试数据
  {
    "storeId": NumberLong(4),
    "name": "爱宠之家 (朝阳店)",
    "location": {
      "type": "Point", 
      "coordinates": [116.4133, 39.9110] // [经度, 纬度] 朝阳区
    }
  },
  {
    "storeId": NumberLong(5),
    "name": "萌宠乐园 (海淀店)",
    "location": {
      "type": "Point",
      "coordinates": [116.3380, 39.9860] // [经度, 纬度] 海淀区
    }
  },
  {
    "storeId": NumberLong(6),
    "name": "宠物天地 (西城店)",
    "location": {
      "type": "Point", 
      "coordinates": [116.3770, 39.9088] // [经度, 纬度] 西城区
    }
  }
]);

// 验证数据插入
print("已插入店铺数量: " + db.stores_location.countDocuments());

// 测试地理位置查询 - 查找天安门附近5公里的店铺
print("=== 测试查询天安门附近5公里的店铺 ===");
db.stores_location.find({
  location: {
    $near: {
      $geometry: {
        type: "Point",
        coordinates: [116.404, 39.915] // 天安门坐标
      },
      $maxDistance: 5000 // 5公里
    }
  }
}).forEach(function(doc) {
  print("店铺: " + doc.name + ", 坐标: " + JSON.stringify(doc.location.coordinates));
});

print("MongoDB测试数据插入完成！"); 