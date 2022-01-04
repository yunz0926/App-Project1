# readme

# 1. 연락처

### 주요기능
1. 연락처 불러오기
2. 연락처 세부정보 보여주기
3. 연락처 추가하기
4. 연락처 삭제하기
5. 연락처 검색하기

<br/>

### 1. 연락처 불러오기

앱이 종료되어도 연락처 정보를 저장하는 database를 구현하기 위해서 SQLiteOpenHelper class를 사용했습니다.
또한 연락처를 list로 보여주기 위해 ArrayList 객체를 사용해 recyclerview를 통해 보여주었습니다.
1) 메인 화면이 실행될 때마다 연락처 정보가 담긴 list를 초기화시킨다.
2) database에서 가장 최신의 연락처 정보를 list로 불러온다.
3) 해당 list를 adapter로 보내서 list 형식으로 이름만 보여준다.

```java
userDatabaseHelper = UserDatabaseHelper.getInstance(this);
        database = userDatabaseHelper.getWritableDatabase();

        itemList.clear();
        selectData(TABLE_NAME);

        rv = (RecyclerView) findViewById(R.id.main_rv);
        rv.addItemDecoration(dividerItemDecoration);
        llm = new LinearLayoutManager(this);
        adapter = new RvAdapter(this, itemList);

        rv.setHasFixedSize(true);
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);
```

![contact](https://user-images.githubusercontent.com/80109309/148054706-1af09d37-eb64-4b94-8adf-86901bf7b28f.gif)

<br/>

### 2. 연락처 세부정보 보여주기

<메인 화면 --> 연락처 세부 정보 보여주는 page로 넘어가기
연락처 정보 중 이름 정보만 보여주는 list에서 보고싶은 연락처를 터치하면 ItemActivity.java가 관리하는 item_detail.xml로 넘어가도록 하였습니다. 이때 onClick 함수와 Intent 객체 및 startActivity 함수를 사용하였습니다.

<연락처 세부 정보 보여주는 page>
1) 'GO BACK' 버튼  --> 메인 화면으로 넘어간다
2) 'DELETE' 버튼   --> 해당 연락처를 삭제하고 메인 화면으로 넘어간다
3) 기본 프로필
4) 이름
5) 전화 버튼        --> 전화할 수 있는 앱으로 넘어간다
6) 전화번호
7) 이메일
8) 직업

/-----------------------------------RvAdapter.java-------------------------------------/
```java
public void onBindViewHolder(CustomViewHolder holder, final int position) {
        final Item item = filteredList.get(position);
        holder.name.setText(item.getItem_name());

        holder.card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                intent = new Intent(v.getContext(), ItemActivity.class);
                intent.putExtra("name", item.getItem_name());
                intent.putExtra("number", item.getItem_number());
                intent.putExtra("email", item.getItem_email());
                intent.putExtra("job", item.getItem_job());
                v.getContext().startActivity(intent);
                Toast.makeText(v.getContext(), "clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
```

/-----------------------------------------------------------------------------------------/

/-----------------------------------ItemActivity.java-------------------------------------/
```java
intent = getIntent();
        name = intent.getStringExtra("name");
        number = intent.getStringExtra("number");
        email = intent.getStringExtra("email");
        job = intent.getStringExtra("job");

text_name = findViewById(R.id.item_detail_name);
        text_number = findViewById(R.id.item_detail_number);
        text_email = findViewById(R.id.item_detail_email);
        text_job = findViewById(R.id.item_detail_job);

text_name.setText(name);
        text_number.setText(spannable_number);
        text_email.setText(spannable_email);
        text_job.setText(spannable_job);
```

/-----------------------------------------------------------------------------------------/

![detail_item](https://user-images.githubusercontent.com/80109309/148054775-db9ae37b-9655-426e-a288-17df3a8b06cf.gif)

![go_back](https://user-images.githubusercontent.com/80109309/148054564-c438bda2-6dbc-485e-9db1-a601e2abd96c.gif)

![call](https://user-images.githubusercontent.com/80109309/148058442-4454c9dd-8631-4cc4-96bd-5036cb9f7e04.gif)

<br/>


### 3. 연락처 추가하기

<메인 화면 --> 연락처를 추가하는 page로 넘어가기>
MainActivity.java가 관리하는 activity_main.xml의 '+' 버튼을 누르면 메인 화면으로 넘어가도록 하였습니다. 이때 onClick 함수와 Intent 객체 및 startActivity 함수를 사용하였습니다.

<연락처를 추가하는 page에서 연락처 추가하기>
핸드폰의 앱이 종료되어도 연락처가 저장이 될 수 있도록 SQLiteOpenHelper class를 사용해서 database를 구현하였습니다. 연락처를 추가하는 page(add_item.xml)에서 이름(name), 전화번호(number), 이메일(email), 직업(job)을 입력하고 'ADD' 버튼을 누르면 database에 해당 정보들이 저장이 되고 다시 MainActivity.java가 관리하는 activity_main.xml로 돌아오도록 하였습니다.

<연락처를 추가하는 page --> 메인 화면>
'ADD' 버튼을 눌러서 데이터가 추가되거나 'GO BACK' 버튼을 누르면 메인화면으로 넘어가도록 하였습니다.

/-----------------------------------MainActivity.java-------------------------------------/
```java
Button add_Btn = (Button) findViewById(R.id.add_Btn);
        add_Btn.setOnClickListener(new View.OnCli
        ckListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddActivity.class);
                startActivity(intent);
            }
        });
```
/-----------------------------------------------------------------------------------------/

/-----------------------------------AddActivity.java-------------------------------------/
```java
add_item_add_Btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                name = add_name.getText().toString();
                number = (String) add_number.getText().toString();
                email = add_email.getText().toString();
                job = add_job.getText().toString();
                if (name.equals("")) {
                    insertData("'" + number + "'", "'" + number + "'", "'" + email + "'", "'" + job + "'");
                } else {
                    insertData("'" + name + "'", "'" + number + "'", "'" + email + "'", "'" + job + "'");
                }
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
                Toast.makeText(v.getContext(), "added", Toast.LENGTH_SHORT).show();
            }
        });
```
/-----------------------------------------------------------------------------------------/

![add](https://user-images.githubusercontent.com/80109309/148056438-3aa1c3b0-6dbf-401b-97ab-ba0b357a5c63.gif)

<br/>

### 4. 연락처 삭제하기

연락처 세부 정보를 보여주는 page에서 'DELETE' 버튼을 터치하면 database에서 연락처 정보를 삭제한 다음 메인 화면으로 넘어간다

/-----------------------------------ItemActivity.java-------------------------------------/
```java
Button deleteBtn = findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteData(name, number, email, job);
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
                Toast.makeText(v.getContext(), "deleted", Toast.LENGTH_SHORT).show();
            }
        });
```
/-----------------------------------------------------------------------------------------/

![delete](https://user-images.githubusercontent.com/80109309/148054875-a66553b9-322c-4fc2-91f4-6ccb5e3778c8.gif)

<br/>

### 5. 연락처 검색하기

메인 화면에서 'search' 부분을 클릭하면 이름 정보를 통해 연락처를 검색할 수 있습니다.

![search](https://user-images.githubusercontent.com/80109309/148054926-14b706f0-f52e-466a-a270-5eaad4c19508.gif)

<br/>

# 2. 갤러리
### 주요기능
1. GridView를 이용한 썸네일 보여주기
2. 디테일한 이미지 뷰 보여주기
3. 롱 클릭시 삭제 팝업 띄우기
4. 휴대폰 내장 갤러리로부터 이미지 불러와 추가하기

<br/>

### 1. GridView를 이용한 썸네일 보여주기

갤러리의 첫 화면에서는 이미지들의 썸네일을 격자로 보여주기 위해 GridVeiw를 사용하였다.

```java
GridView gridView = (GridView) findViewById(R.id.grid_view);
imageAdapter = new ImageAdapter(this, displayWidth);
gridView.setAdapter(imageAdapter);
```
GridView를 선언하고 선언 된 GridView에 imageAdapter 연결하였다. 

<br/>


### 2. 디테일한 이미지 뷰 보여주기

갤러리 썸네일에서 이미지를 클릭하면 FullImageActivity를 실행하여 디테일한 이미지 뷰를 보여줄 수 있도록 하였다.

```java
gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), FullImageActivity.class);
                i.putExtra("id", position);
                startActivity(i);
            }
        });
```
FullImageActivity에서는 선택 된 이미지 아이템의 position을 이용해서 이미지 데이터 배열에서 이미지를 가져와 화면 전체에 채워진 뷰를 보여준다. 
또한 PhotoViewAttacher을 이용해서 핀치 줌/아웃 기능을 추가하였다.

```java
public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image);

        // get intent data
        Intent i = getIntent();

        // Selected image id
        int position = i.getExtras().getInt("id");
        com.example.project1_2.ImageAdapter imageAdapter = new com.example.project1_2.ImageAdapter(this, 360);

        PhotoViewAttacher mAttacher;
        ImageView imageView = (ImageView) findViewById(R.id.full_image_view);

        if(imageAdapter.mThumblds[position] instanceof Integer){
            imageView.setImageResource((Integer)imageAdapter.mThumblds[position]);
            mAttacher = new PhotoViewAttacher(imageView);

        }
        else if(imageAdapter.mThumblds[position] instanceof Uri){
            imageView.setImageURI((Uri)imageAdapter.mThumblds[position]);
            mAttacher = new PhotoViewAttacher(imageView);
        }
    }
```
<br/>


### 3. 롱 클릭시 삭제 팝업 띄우기

갤러리 썸네일에서 이미지를 롱클릭했을 시 삭제를 할 수 있도록 PopupMenu를 사용하였다. 

```java
gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                System.out.println("long click");
                PopupMenu popup = new PopupMenu(MainActivity.this, view);
                getMenuInflater().inflate(R.menu.list_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_delete:
                                imageAdapter.deleteItem(position);
                                imageAdapter.notifyDataSetChanged();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });

                popup.show();
                return true;
            }
        });
```

![20220104_182930](https://user-images.githubusercontent.com/59657560/148052705-836d32ca-ad7e-4c6a-8b1e-381ff5925e9d.gif)
<br/>

### 4. 휴대폰 내장 갤러리로부터 이미지 불러와 추가하기

어플을 처음 설치하면 로컬 디렉토리로부터 불러온 약 20장의 사진이 미리 저장되어있지만, 휴대폰의 내장 갤러리로부터 사진을 불러와 추가할 수 있도록 기능을 구현하였다.

이를 위해서 AndroidManifest 파일에 휴대폰의 저장소에 접근할 수 있는 권한을 추가해주었고, insert 버튼을 눌렀을 시 내장 갤러리로 넘어가서 이미지를 가져올 수 있다.

```java
Button btn_insert = (Button) findViewById(R.id.btn_insert);
        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                    startActivityForResult(intent, PICK_IMAGE);
                }
            }
        });
```
<br/>

# 3. 날씨에 맞는 옷차림 추천
### 주요기능
1. 위치 정보와 openWeatherAPI를 이용해 날씨 정보 가져오기
2. 3시간동안의 날씨 데이터 처리
3. 탭을 통해 시간 별 날씨 정보와 해당 날씨에 맞는 옷차림을 보여주기

![KakaoTalk_Image_2022-01-04-21-23-51](https://user-images.githubusercontent.com/80109309/148058572-65a84aa0-2a3e-460b-bc89-b9af3207e485.jpeg)

<br/>

### 1. 위치 정보와 openWeatherAPI를 이용해 날씨 정보 가져오기

사용자 주변의 날씨 데이터를 불러오기 위해 먼저 위치 권한 허용 요청을 하고 허용됐을 시 사용자의 위치정보를 가져온다.
위도, 경도 정보를 넣어 openWeatherAPI를 이용해 24시간동안의 날씨데이터를 1시간 단위로 가져온다.
위치 권한과 http 연결을 위해 AndroidManifest파일에 다음과 같은 권한을 추가해주었다.

```java
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

```java
if(ContextCompat.checkSelfPermission(c, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            String locationProvider = LocationManager.NETWORK_PROVIDER;
            Location location = lm.getLastKnownLocation(locationProvider);

            longitude = location.getLongitude();
            latitude = location.getLatitude();

            String queryURL = "http://api.openweathermap.org/data/2.5/onecall?lat=" + latitude + "&lon=" + longitude + "&exclude=current,minutely,daily,alerts&appid=260c05833f6d6608df17de1271ec4d50";
            try{
                URL url = new URL(queryURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(getStringFromInputStream(in));
                JSONObject json = (JSONObject)obj;
                weatherList = parseJSON(json);
            } catch(MalformedURLException e){
                System.err.println("Malformed URL");
                e.printStackTrace();
            } catch(JSONException e) {
                System.err.println("JSON parsing error");
                e.printStackTrace();
            } catch(IOException e){
                System.err.println("URL Connection failed");
                e.printStackTrace();
            } catch(ParseException e){
                System.err.println("Parse Excepption");
                e.printStackTrace();
            }

        }
        return weatherList;
    }
```

<br/> 

### 2. 시간 별 날씨 데이터 처리하기

JSON 데이터 파일을 처리해서 필요한 데이터만 추출하여 ArrayList에 저장하였고, 3시간 단위로 날씨 정보를 처리하였다.
기온의 경우 세시간 중 최저 온도를 기준으로 설정하였고, 세시간 중에 Rain, Snow, Thunderstorm인 시간이 있는 경우 mainWeather을 Rain, Snow, Thunderstorm 으로 설정하였다.
그외의 경우에는 세시간 중 중간 시간대의 날씨를 mainWeather로 설정하였다.

<br/>

### 3. 탭을 통해 시간 별 날씨 정보와 해당 날씨에 맞는 옷차림을 보여주기

탭으로 시간을 선택하면 시간 별 날씨 정보를 가져와서 그에 맞는 옷차림을 보여준다. 

![20220104_185423](https://user-images.githubusercontent.com/59657560/148053011-139883e6-f834-4dd3-b938-d0b0194f2296.gif)

![20220104_185543](https://user-images.githubusercontent.com/59657560/148053083-f9019da7-e141-4a5f-a18b-75786cc73b6b.gif)

![20220104_190018](https://user-images.githubusercontent.com/59657560/148053139-823cca0f-6df1-4f34-8d5d-b983712eae2f.gif)

![20220104_190137](https://user-images.githubusercontent.com/59657560/148053176-f53c089f-e459-440f-b099-411168922d22.gif)

![20220104_200741](https://user-images.githubusercontent.com/59657560/148053257-949c75e0-ea27-46cf-97bc-c7a1538ba827.gif)

![20220104_200249](https://user-images.githubusercontent.com/59657560/148053207-5e54c779-8f44-4779-9644-b37d835e690c.gif)

![20220104_200859](https://user-images.githubusercontent.com/59657560/148053302-6f1413ad-6134-412f-825e-88d0909fcb5b.gif)







