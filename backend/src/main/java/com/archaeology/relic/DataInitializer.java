package com.archaeology.relic;

import com.archaeology.relic.entity.Coordinate3D;
import com.archaeology.relic.entity.ExcavationUnit;
import com.archaeology.relic.entity.Relic;
import com.archaeology.relic.entity.RestorationRecord;
import com.archaeology.relic.repository.ExcavationUnitRepository;
import com.archaeology.relic.repository.RelicRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ExcavationUnitRepository unitRepo;
    private final RelicRepository relicRepo;

    public DataInitializer(ExcavationUnitRepository unitRepo, RelicRepository relicRepo) {
        this.unitRepo = unitRepo;
        this.relicRepo = relicRepo;
    }

    @Override
    public void run(String... args) {
        if (unitRepo.count() > 0) return;

        // ---- 5 个探方 ----
        List<ExcavationUnit> units = new ArrayList<>();
        units.add(createUnit("T2025-001", "遗址A区东侧", 5.0, 5.0, 2.3, "发掘中", "张建国"));
        units.add(createUnit("T2025-002", "遗址A区西侧", 4.0, 4.0, 1.8, "发掘中", "李华"));
        units.add(createUnit("T2025-003", "遗址B区南侧", 6.0, 5.0, 3.1, "已完成", "王磊"));
        units.add(createUnit("T2025-004", "遗址B区北侧", 5.0, 5.0, 2.5, "暂停", "陈芳"));
        units.add(createUnit("T2025-005", "遗址C区中部", 8.0, 6.0, 1.5, "发掘中", "刘明"));

        for (ExcavationUnit u : units) {
            unitRepo.save(u);
        }

        // ---- 12 件遗物，分布到不同探方 ----
        List<Relic> relics = new ArrayList<>();
        relics.add(createRelic("RW-2025-0001", "青铜鼎", "青铜器", "战国晚期青铜鼎，保存较完整", "青铜",
                "战国", LocalDate.of(2025, 3, 15), "张建国", units.get(0),
                "东壁第3层", "完好", 10.5, 8.2, -2.1));
        relics.add(createRelic("RW-2025-0002", "陶罐", "陶器", "灰陶双耳罐，口径12cm", "陶",
                "西汉", LocalDate.of(2025, 3, 20), "张建国", units.get(0),
                "东南角第2层", "轻微破损", 11.2, 7.8, -1.9));
        relics.add(createRelic("RW-2025-0003", "玉璧", "玉器", "青玉璧，直径8cm，表面有谷纹", "玉",
                "商周", LocalDate.of(2025, 4, 2), "李华", units.get(1),
                "西壁第4层", "完好", 8.3, 9.5, -2.5));
        relics.add(createRelic("RW-2025-0004", "铜镜", "青铜器", "汉代四神铜镜，背面有铭文", "青铜",
                "汉", LocalDate.of(2025, 4, 10), "李华", units.get(1),
                "北角第2层", "锈蚀", 9.0, 9.8, -1.6));
        relics.add(createRelic("RW-2025-0005", "石斧", "石器", "磨制石斧，长15cm", "石",
                "新石器", LocalDate.of(2025, 4, 18), "王磊", units.get(2),
                "南壁第5层", "轻微磨损", 14.1, 5.3, -3.0));
        relics.add(createRelic("RW-2025-0006", "骨针", "骨器", "细骨针，可能用于缝纫", "骨",
                "商", LocalDate.of(2025, 5, 5), "王磊", units.get(2),
                "中部第4层", "断裂", 6.8, 12.1, -2.8));
        relics.add(createRelic("RW-2025-0007", "铁剑", "铁器", "战国铁剑，剑身锈蚀但剑格完好", "铁",
                "战国", LocalDate.of(2025, 5, 12), "王磊", units.get(2),
                "西南角第3层", "严重锈蚀", 3.5, 2.1, -2.9));
        relics.add(createRelic("RW-2025-0008", "彩陶片", "陶器", "仰韶文化彩陶残片，有几何纹饰", "陶",
                "仰韶", LocalDate.of(2025, 5, 20), "陈芳", units.get(3),
                "东北角第6层", "残缺", 4.2, 3.1, -2.3));
        relics.add(createRelic("RW-2025-0009", "金饰件", "金器", "小型金箔饰件，推测为冠饰", "金",
                "唐", LocalDate.of(2025, 6, 1), "陈芳", units.get(3),
                "中部第2层", "完好", 12.5, 7.0, -1.2));
        relics.add(createRelic("RW-2025-0010", "瓷碗", "瓷器", "宋代青瓷碗，釉色青绿", "瓷",
                "宋", LocalDate.of(2025, 6, 8), "刘明", units.get(4),
                "东壁第2层", "完整", 5.0, 8.5, -1.3));
        relics.add(createRelic("RW-2025-0011", "竹简", "有机质", "战国竹简残片，有墨书文字", "竹",
                "战国", LocalDate.of(2025, 6, 12), "刘明", units.get(4),
                "西壁第1层", "炭化、残缺", 3.8, 2.5, -0.9));
        relics.add(createRelic("RW-2025-0012", "铜钱", "钱币", "唐代开元通宝一串", "铜",
                "唐", LocalDate.of(2025, 6, 15), "刘明", units.get(4),
                "南角第2层", "锈蚀", 13.0, 6.3, -1.5));

        // 给其中几件遗物加修复记录
        RestorationRecord r1 = new RestorationRecord();
        r1.setRelic(relics.get(3)); // 铜镜
        r1.setRecordDate(LocalDate.of(2025, 5, 1));
        r1.setOperator("赵修复");
        r1.setDamageDescription("铜镜背面边缘局部缺失，镜面多处锈蚀斑点");
        r1.setRestorationMethod("机械除锈后化学封护，缺失部分用环氧树脂补全");
        r1.setMaterialsUsed("环氧树脂、BTA缓蚀剂、Paraloid B72");
        r1.setRestorationEffect("除锈效果良好，补全部分颜色协调");
        relics.get(3).setRestorationRecords(List.of(r1));

        RestorationRecord r2 = new RestorationRecord();
        r2.setRelic(relics.get(5)); // 骨针
        r2.setRecordDate(LocalDate.of(2025, 5, 20));
        r2.setOperator("赵修复");
        r2.setDamageDescription("骨针中部横向断裂，断口处有粉末化趋势");
        r2.setRestorationMethod("使用Paraloid B72进行粘接加固");
        r2.setMaterialsUsed("Paraloid B72丙酮溶液");
        r2.setRestorationEffect("粘接牢固，但断口处强度仍需观察");
        relics.get(5).setRestorationRecords(List.of(r2));

        for (Relic r : relics) {
            relicRepo.save(r);
        }
    }

    private ExcavationUnit createUnit(String no, String loc, double len, double wid, double dep,
                                       String status, String director) {
        ExcavationUnit u = new ExcavationUnit();
        u.setUnitNo(no);
        u.setLocation(loc);
        u.setLength(len);
        u.setWidth(wid);
        u.setDepth(dep);
        u.setStatus(status);
        u.setDirector(director);
        u.setRemark("");
        return u;
    }

    private Relic createRelic(String no, String name, String cat, String desc, String mat,
                               String era, LocalDate date, String exc, ExcavationUnit unit,
                               String stratum, String preservation, double x, double y, double z) {
        Relic r = new Relic();
        r.setRelicNo(no);
        r.setName(name);
        r.setCategory(cat);
        r.setDescription(desc);
        r.setMaterial(mat);
        r.setEra(era);
        r.setExcavateDate(date);
        r.setExcavator(exc);
        r.setExcavationUnit(unit);
        r.setStratum(stratum);
        r.setPreservationStatus(preservation);

        Coordinate3D coord = new Coordinate3D();
        coord.setX(x);
        coord.setY(y);
        coord.setZ(z);
        coord.setCoordinateSystem("CGCS2000");
        r.setCoordinate(coord);

        r.setRemark("");
        return r;
    }
}
