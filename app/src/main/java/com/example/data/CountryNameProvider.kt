package com.example.data

enum class Country(
    val code: String,
    val displayName: String,
    val flagEmoji: String,
    val names: List<String>
) {
    BANGLADESH("BD", "Bangladesh", "🇧🇩", listOf(
        "আরাফাত হোসেন", "সিয়াম আহমেদ", "রাকিব হাসান", "তানভীর ইসলাম", "নাঈম হোসেন",
        "মাহফুজ রহমান", "সাকিব আহমেদ", "ইমরান হোসেন", "ফাহিম হাসান", "রিফাত ইসলাম",
        "শাকিল আহমেদ", "তৌহিদুল ইসলাম", "মেহেদী হাসান", "রায়হান হোসেন", "জুবায়ের আহমেদ",
        "নাজমুল হাসান", "সাব্বির রহমান", "আদনান হোসেন", "হাসান মাহমুদ", "নাসির উদ্দিন",
        "ফরহাদ হোসেন", "সোহাগ মিয়া", "মুস্তাফিজুর রহমান", "আবির হোসেন", "রাকিবুল ইসলাম",
        "শাওন আহমেদ", "তানজিম হাসান", "সাইফুল ইসলাম", "আশিকুর রহমান", "জাহিদ হাসান",
        "মারুফ হোসেন", "রুবেল আহমেদ", "নোমান ইসলাম", "ইব্রাহিম হোসেন", "তামিম হাসান",
        "রিফাত হোসেন", "মাসুদ রানা", "কামরুল হাসান", "সাদমান ইসলাম", "রেজাউল করিম",
        "মিজানুর রহমান", "আমিনুল ইসলাম", "শরিফুল হাসান", "ফয়সাল আহমেদ", "সুমন হোসেন",
        "হৃদয় ইসলাম", "আরমান হোসেন", "শামীম আহমেদ", "সোহেল রহমান", "রাশেদুল ইসলাম",
        "অনিক হাসান", "ইশতিয়াক হোসেন", "রুবাইয়াত ইসলাম", "মাহিন আহমেদ", "সাকলাইন হোসেন",
        "নাফিস রহমান", "তাহমিদ হাসান", "ইফতেখার ইসলাম", "মুরসালিন হোসেন", "সামিউল আহমেদ"
    )),

    UNITED_STATES("US", "United States", "🇺🇸", listOf(
        "James Anderson", "Michael Johnson", "William Smith", "David Williams", "Christopher Brown",
        "Matthew Jones", "Daniel Miller", "Joseph Davis", "Andrew Wilson", "Joshua Moore",
        "Ryan Taylor", "Nicholas Thomas", "Tyler Jackson", "Brandon White", "Justin Harris",
        "Kevin Martin", "Brian Thompson", "Steven Garcia", "Jason Martinez", "Robert Robinson",
        "Ethan Clark", "Noah Lewis", "Alexander Lee", "Benjamin Walker", "Samuel Hall",
        "Daniel Allen", "Jacob Young", "Logan Hernandez", "Mason King", "Lucas Wright",
        "Emily Johnson", "Olivia Smith", "Emma Williams", "Sophia Brown", "Ava Jones",
        "Isabella Miller", "Mia Davis", "Charlotte Wilson", "Amelia Moore", "Harper Taylor",
        "Evelyn Anderson", "Abigail Thomas", "Ella Jackson", "Elizabeth White", "Sofia Harris",
        "Avery Martin", "Scarlett Thompson", "Grace Garcia", "Chloe Martinez", "Victoria Robinson",
        "Lily Clark", "Hannah Lewis", "Natalie Lee", "Zoey Walker", "Samantha Hall",
        "Madison Allen", "Brooklyn Young", "Layla King", "Aria Wright", "Riley Hernandez"
    )),

    CHINA("CN", "China", "🇨🇳", listOf(
        "Wei Zhang", "Ming Li", "Jun Wang", "Hao Chen", "Yang Liu",
        "Jie Yang", "Lei Huang", "Tao Zhao", "Bo Wu", "Qiang Zhou",
        "Feng Xu", "Peng Sun", "Jian Ma", "Yong Zhu", "Bin Hu",
        "Xin Guo", "Kai He", "Lin Gao", "Chao Lin", "Junjie Luo",
        "Wei Zhang", "Xiaoming Li", "Yifan Wang", "Zhihao Chen", "Haoyu Liu",
        "Yuchen Yang", "Zihan Huang", "Yuxuan Zhao", "Cheng Wu", "Zhen Zhou",
        "Li Na", "Fang Zhang", "Mei Li", "Jing Wang", "Yan Chen",
        "Xue Liu", "Min Yang", "Ying Huang", "Juan Zhao", "Hui Wu",
        "Lan Zhou", "Xin Xu", "Jie Sun", "Na Wang", "Ting Zhang",
        "Yue Chen", "Yanling Li", "Xiaoyu Wang", "Lili Zhang", "Wenjing Liu",
        "Qian Yang", "Xinyi Huang", "Yuting Zhao", "Jiaqi Wu", "Shanshan Zhou",
        "Mengyao Xu", "Ruoxi Sun", "Yuhan Li", "Zixuan Wang", "Yilin Chen"
    )),

    SAUDI_ARABIA("SA", "Saudi Arabia", "🇸🇦", listOf(
        "محمد أحمد", "أحمد محمود", "عبد الله حسن", "عمر محمد", "يوسف علي",
        "خالد حسن", "محمود إبراهيم", "إبراهيم أحمد", "عبد الرحمن محمد", "مصطفى محمود",
        "ياسر أحمد", "سامر حسن", "كريم عبد الله", "طارق محمد", "حمزة علي",
        "أنس محمود", "معاذ أحمد", "زياد حسن", "سليم محمد", "رامي عبد الله",
        "فاطمة أحمد", "مريم محمد", "نور حسن", "سارة محمود", "آمنة علي",
        "زينب أحمد", "ليان محمد", "هبة حسن", "سلمى محمود", "ريم عبد الله",
        "ندى أحمد", "دعاء محمد", "إيمان علي", "آية حسن", "منى محمود",
        "رنا أحمد", "ياسمين محمد", "حنان عبد الله", "مريم علي", "سارة حسن",
        "عبد العزيز أحمد", "عبد الكريم محمد", "عبد الرحيم حسن", "عبد الملك علي", "صالح محمود",
        "فهد أحمد", "ناصر محمد", "سلمان حسن", "وليد عبد الله", "عادل محمود",
        "حسان علي", "رائد أحمد", "باسل محمد", "مازن حسن", "فراس محمود",
        "عمار عبد الله", "سيف أحمد", "إياد محمد", "زاهر حسن", "هاني علي"
    )),

    FRANCE("FR", "France", "🇫🇷", listOf(
        "Jean Martin", "Pierre Bernard", "Louis Dubois", "Thomas Moreau", "Julien Laurent",
        "Nicolas Simon", "Antoine Michel", "Alexandre Lefebvre", "Gabriel Leroy", "Hugo Roux",
        "Lucas Fournier", "Arthur Girard", "Nathan Bonnet", "Maxime Dupont", "Victor Lambert",
        "Adrien Fontaine", "Romain Rousseau", "Paul Vincent", "Julien Chevalier", "Mathieu Robin",
        "Marie Martin", "Camille Bernard", "Emma Dubois", "Chloé Moreau", "Léa Laurent",
        "Manon Simon", "Clara Michel", "Julie Lefebvre", "Alice Leroy", "Louise Roux",
        "Émilie Fournier", "Sophie Girard", "Inès Bonnet", "Sarah Dupont", "Amélie Lambert",
        "Pauline Fontaine", "Charlotte Rousseau", "Juliette Vincent", "Élise Chevalier", "Marion Robin",
        "Benjamin Martin", "Alexandre Bernard", "Thomas Dubois", "Guillaume Moreau", "Sébastien Laurent",
        "Olivier Simon", "François Michel", "Rémi Lefebvre", "Baptiste Leroy", "Clément Roux",
        "Jeanne Fournier", "Margaux Girard", "Mathilde Bonnet", "Valentine Dupont", "Anaïs Lambert",
        "Céline Fontaine", "Noémie Rousseau", "Élodie Vincent", "Amandine Chevalier", "Gabrielle Robin"
    )),

    INDIA("IN", "India (Hindi)", "🇮🇳", listOf(
        "आरव शर्मा", "विवान वर्मा", "आदित्य सिंह", "अर्जुन कुमार", "रोहन गुप्ता",
        "राहुल यादव", "अमित मिश्रा", "अक्षय चौहान", "करण मेहता", "वरुण सक्सेना",
        "मोहित अग्रवाल", "निखिल जैन", "अभिषेक राजपूत", "आकाश तिवारी", "शिवम पांडे",
        "अंकित शुक्ला", "सौरभ त्रिपाठी", "दीपक श्रीवास्तव", "मनीष ठाकुर", "राजीव भट्ट",
        "अनन्या शर्मा", "आर्या वर्मा", "सिया सिंह", "अनुष्का कुमार", "प्रिया गुप्ता",
        "नेहा यादव", "पूजा मिश्रा", "काव्या चौहान", "रिया मेहता", "दिव्या सक्सेना",
        "श्रेया अग्रवाल", "तन्वी जैन", "आयुषी राजपूत", "पायल तिवारी", "राधिका पांडे",
        "निशा शुक्ला", "साक्षी त्रिपाठी", "स्वाति श्रीवास्तव", "मीनाक्षी ठाकुर", "कृति भट्ट",
        "देव शर्मा", "युवराज वर्मा", "आर्यन सिंह", "दक्ष कुमार", "समीर गुप्ता",
        "मयंक यादव", "हर्ष मिश्रा", "विवेक चौहान", "रितेश मेहता", "वरुण अग्रवाल",
        "मुस्कान शर्मा", "नेहा वर्मा", "तनु सिंह", "मानसी कुमार", "आकांक्षा गुप्ता",
        "सोनिया यादव", "ज्योति मिश्रा", "स्नेहा चौहान", "भावना मेहता", "रितिका अग्रवाल"
    )),

    MADAGASCAR("MG", "Madagascar", "🇲🇬", listOf(
        "Jean Rakoto", "Andry Randria", "Hery Rasoanaivo", "Tiana Razafindrakoto", "Fetra Rakotomalala",
        "Tahina Randrianasolo", "Mamy Raveloson", "Tojo Rakotondrabe", "Niry Ramanantsoa", "Lova Razafimahatratra",
        "Solofo Rakotoarisoa", "Njaka Randrianarisoa", "Faly Ramaroson", "Toky Rakotobe", "Zo Andrianjafy",
        "Hasina Razanadrakoto", "Miora Rakotondramasy", "Voahirana Randriamampianina", "Hanitra Rasoazanany", "Fanja Razafindrazaka",
        "Soa Rakotozafy", "Lalao Randrianantenaina", "Anja Ravelomanana", "Malala Rakotondrabe", "Nantenaina Razafindrakoto",
        "Tahina Rakotoarisoa", "Vola Randrianasolo", "Tsiky Rasoanaivo", "Mirana Razafimahatratra", "Sarobidy Rakotomalala",
        "Haja Andrianjafy", "Fenitra Ramaroson", "Kanto Rakotobe", "Iary Ramanantsoa", "Miora Randria",
        "Tovo Rakoto", "Harena Razanadrakoto", "Ony Rakotondramasy", "Mamy Rasoazanany", "Noro Razafindrazaka",
        "Ando Rakotozafy", "Soary Randrianantenaina", "Finaritra Ravelomanana", "Tsiory Rakotondrabe", "Hasina Razafindrakoto",
        "Faneva Rakotoarisoa", "Tantely Randrianasolo", "Lanto Rasoanaivo", "Aina Razafimahatratra", "Miary Rakotomalala",
        "Tsanta Andrianjafy", "Hoby Ramaroson", "Zo Rakotobe", "Fitia Ramanantsoa", "Hanta Randria",
        "Kolo Rakoto", "Ny Aina Razanadrakoto", "Elia Rakotondramasy", "Fara Rasoazanany", "Manda Razafindrazaka"
    ));

    fun getRandomFirstAndLastName(): Pair<String, String> {
        val fullName = names.random()
        val parts = fullName.trim().split(" ")
        val fname = parts.first()
        val lname = if (parts.size > 1) parts.drop(1).joinToString(" ") else "Hossain"
        return Pair(fname, lname)
    }

    companion object {
        fun fromCode(code: String): Country {
            return values().find { it.code.equals(code, ignoreCase = true) } ?: BANGLADESH
        }
    }
}
