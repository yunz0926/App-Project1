# readme

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







