package data

import (
	"bytes"
	"encoding/json"
	"errors"
	"fmt"
	"github.com/google/uuid"
	"github.com/stretchr/testify/require"
	"io"
	"net/http"
	"strconv"
	"testing"
	"time"
)

const address = "http://localhost:8080"

const (
	apiV            = "/api/v1"
	authBase        = apiV + "/auth"
	userBase        = apiV + "/user"
	courseBase      = apiV + "/courses"
	registrationUrl = authBase + "/sign-up"
	login           = authBase + "/sign-in"
)

var client = http.Client{
	Timeout: 10 * time.Minute,
}

func TestPreflight(t *testing.T) {
	require.Equal(t, true, true)
}

type RegistrationBody struct {
	FirstName   string    `json:"firstName"`
	LastName    string    `json:"lastName"`
	Email       string    `json:"email"`
	Password    string    `json:"password"`
	PhoneNumber string    `json:"phoneNumber"`
	CompanyName string    `json:"companyName,omitempty"`
	CourseUUID  uuid.UUID `json:"courseUUID"`
}

type LoginRequest struct {
	Email    string `json:"email"`
	Password string `json:"password"`
}

type JwtAuthenticationResponse struct {
	Token string `json:"token"`
}
type ApplicationResponseDto struct {
	Description   string                    `json:"description"`
	Price         float64                   `json:"price"`
	ApplicationID int                       `json:"applicationID"`
	Jwt           JwtAuthenticationResponse `json:"jwt"`
}

type ErrHttp struct {
	code int
	text string
}

func (e ErrHttp) Error() string {
	return fmt.Sprintf("{code: %v, error: %v}", e.code, e.text)
}

func (e ErrHttp) getCode() int {
	return e.code
}

func (e ErrHttp) getText() string {
	return e.text
}

type Course struct {
	CourseUUID     uuid.UUID   `json:"courseUUID"`
	CourseName     string      `json:"courseName"`
	CoursePrice    float64     `json:"coursePrice"`
	Description    string      `json:"description"`
	TopicName      string      `json:"topicName"`
	CreationDate   string      `json:"creationDate"`
	CourseDuration int         `json:"courseDuration"`
	WithJobOffer   bool        `json:"withJobOffer"`
	IsCompleted    interface{} `json:"isCompleted"`
}

type CoursesResponse struct {
	Courses []Course `json:"course_list"`
}

func getAllCourses() ([]Course, error) {
	url := address + courseBase

	req, err := http.NewRequest(http.MethodGet, url, nil)
	if err != nil {
		return nil, fmt.Errorf("ошибка при создании запроса")
	}

	resp, err := client.Do(req)

	if err != nil {
		return nil, fmt.Errorf("ошибка выполнения запроса: %w", err)
	}

	if resp.StatusCode > 300 || resp.StatusCode < 200 {
		bodyBytes, _ := io.ReadAll(resp.Body)
		return nil, ErrHttp{
			code: resp.StatusCode,
			text: string(bodyBytes),
		}
	}

	var coursesResp CoursesResponse
	if err := json.NewDecoder(resp.Body).Decode(&coursesResp); err != nil {
		return nil, fmt.Errorf("ошибка при обработке ответа: %v", err)
	}

	return coursesResp.Courses, nil
}

func createCourse(token string, course Course) error {
	url := address + courseBase

	reqBody, err := json.Marshal(course)
	if err != nil {
		return fmt.Errorf("ошибка маршалинга запроса: %w", err)
	}
	req, err := http.NewRequest(http.MethodPost, url, bytes.NewBuffer(reqBody))
	if err != nil {
		return fmt.Errorf("ошибка создания запроса: %w", err)
	}
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Accept", "application/json")
	req.Header.Set("Authorization", "Bearer "+token)

	fmt.Println(req)
	resp, err := client.Do(req)
	if err != nil {
		return fmt.Errorf("ошибка выполнения запроса: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode < 200 || resp.StatusCode > 300 {
		bodyBytes, _ := io.ReadAll(resp.Body)
		var httpErr ErrHttp
		httpErr.code = resp.StatusCode
		httpErr.text = string(bodyBytes)
		return httpErr
	}

	return nil
}

func signUp(reg RegistrationBody) (*JwtAuthenticationResponse, error) {
	url := address + registrationUrl

	requestBody, err := json.Marshal(reg)
	if err != nil {
		return nil, fmt.Errorf("ошибка маршалинга запроса: %w", err)
	}

	req, err := http.NewRequest(http.MethodPost, url, bytes.NewBuffer(requestBody))
	if err != nil {
		return nil, fmt.Errorf("ошибка создания запроса: %w", err)
	}
	req.Header.Set("Content-Type", "application/json")

	resp, err := client.Do(req)
	if err != nil {
		return nil, fmt.Errorf("ошибка выполнения запроса: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode > 300 || resp.StatusCode < 200 {
		bodyBytes, _ := io.ReadAll(resp.Body)
		return nil, ErrHttp{
			code: resp.StatusCode,
			text: string(bodyBytes),
		}
	}

	var applicationResponse JwtAuthenticationResponse
	err = json.NewDecoder(resp.Body).Decode(&applicationResponse)
	if err != nil {
		return nil, fmt.Errorf("ошибка декодирования ответа: %w", err)
	}

	return &applicationResponse, nil
}

func getCourse(token string, courseUUID uuid.UUID) (Course, error) {
	url := address + courseBase + "/" + courseUUID.String()

	req, err := http.NewRequest(http.MethodGet, url, nil)
	if err != nil {
		return Course{}, fmt.Errorf("ошибка при создании запроса")
	}
	req.Header.Set("Authorization", "Bearer "+token)

	resp, err := client.Do(req)

	if err != nil {
		return Course{}, fmt.Errorf("ошибка выполнения запроса: %w", err)
	}

	if resp.StatusCode > 300 || resp.StatusCode < 200 {
		bodyBytes, _ := io.ReadAll(resp.Body)
		return Course{}, ErrHttp{
			code: resp.StatusCode,
			text: string(bodyBytes),
		}
	}

	var course Course
	if err := json.NewDecoder(resp.Body).Decode(&course); err != nil {
		return Course{}, fmt.Errorf("ошибка при обработке ответа: %v", err)
	}

	return course, nil

}

func signIn(reg LoginRequest) (*JwtAuthenticationResponse, error) {
	url := address + login

	requestBody, err := json.Marshal(reg)
	if err != nil {
		return nil, fmt.Errorf("ошибка маршалинга запроса: %w", err)
	}
	req, err := http.NewRequest(http.MethodPost, url, bytes.NewBuffer(requestBody))
	if err != nil {
		return nil, fmt.Errorf("ошибка создания запроса: %w", err)
	}
	req.Header.Set("Content-Type", "application/json")

	resp, err := client.Do(req)
	if err != nil {
		return nil, fmt.Errorf("ошибка выполнения запроса: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode < 200 || resp.StatusCode > 300 {
		bodyBytes, _ := io.ReadAll(resp.Body)
		return nil, fmt.Errorf("неожиданный код ответа: %d, тело: %s", resp.StatusCode, string(bodyBytes))
	}

	var jwtAuthResponse JwtAuthenticationResponse
	err = json.NewDecoder(resp.Body).Decode(&jwtAuthResponse)
	if err != nil {
		return nil, fmt.Errorf("ошибка декодирования ответа: %w", err)
	}

	return &jwtAuthResponse, nil
}

func createApplication(uuid uuid.UUID, token string) (int, error) {
	var errHttp ErrHttp
	url := fmt.Sprintf("%s%s/application/%v", address, userBase, uuid)

	req, err := http.NewRequest(http.MethodPost, url, nil)
	if err != nil {
		return 0, fmt.Errorf("ошибка создания запроса: %w", err)
	}
	req.Header.Add("Authorization", "Bearer "+token)

	resp, err := client.Do(req)

	if err != nil {
		return 0, fmt.Errorf("ошибка выполнения запроса: %w", err)
	}

	if resp.StatusCode != http.StatusOK {
		bodyBytes, _ := io.ReadAll(resp.Body)
		errHttp.code = resp.StatusCode
		errHttp.text = string(bodyBytes)
		return 0, errHttp
	}
	bodyBytes, _ := io.ReadAll(resp.Body)

	id, _ := strconv.Atoi(string(bodyBytes))
	return id, nil
}

func updateApplicationStatus(applicationID int, token, newStatus string) error {
	url := address + userBase + "/application/status/" + strconv.Itoa(applicationID)

	type updApplStatusBody struct {
		NewStatus string `json:"newStatus"`
	}
	stat := updApplStatusBody{NewStatus: newStatus}

	body, err := json.Marshal(stat)
	if err != nil {
		return err
	}

	req, err := http.NewRequest(http.MethodPatch, url, bytes.NewBuffer(body))

	if err != nil {
		return fmt.Errorf("ошибка при создании запроса")
	}
	req.Header.Set("Content-Type", "application/json")
	req.Header.Add("Authorization", "Bearer "+token)
	resp, err := client.Do(req)

	if err != nil {
		return fmt.Errorf("ошибка выполнения запроса: %w", err)
	}

	if resp.StatusCode > 300 || resp.StatusCode < 200 {
		bodyBytes, _ := io.ReadAll(resp.Body)
		return ErrHttp{
			code: resp.StatusCode,
			text: string(bodyBytes),
		}
	}

	return nil
}

func TestRegistrationWithoutCourseID(t *testing.T) {
	reg := RegistrationBody{
		FirstName:   "Ivan",
		LastName:    "Ivanov",
		Email:       uuid.NewString(),
		Password:    "secret",
		PhoneNumber: "+79001234567",
	}

	response, err := signUp(reg)
	require.NoError(t, err)
	require.NotNil(t, response)

	t.Logf("Registration response: %+v", response)
}

func TestLogin(t *testing.T) {
	type args struct {
		Email    string `json:"email"`
		Password string `json:"password"`
	}

	testCases := []struct {
		name   string
		args   args
		expErr bool
	}{
		{
			name: "valid test",
			args: args{
				Email:    "admin",
				Password: "admin",
			},
			expErr: false,
		},
		{
			name: "user not exist",
			args: args{
				Email:    "da",
				Password: "no",
			},
			expErr: true,
		},
		{
			name: "empty data",
			args: args{
				Email:    "",
				Password: "",
			},
			expErr: true,
		},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			reg := LoginRequest{
				Email:    tc.args.Email,
				Password: tc.args.Password,
			}
			res, err := signIn(reg)
			if tc.expErr {
				require.Error(t, err, tc.name)
			} else {
				require.NotNil(t, res, tc.name)
				require.True(t, len(res.Token) > 5)
			}
		})
	}
}

func TestCreateApplication(t *testing.T) {
	jwtResp, err := signIn(LoginRequest{Email: "jaba@jaba.jaba", Password: "jaba"})
	require.NoError(t, err, "failed to get token")
	token := jwtResp.Token
	t.Log("attempt to create an application with token: " + token)

	courses, err := getAllCourses()
	require.NoError(t, err, "failed to get courses")
	require.True(t, len(courses) != 0, "at least one course must be")

	testCases := []struct {
		name           string
		courseUUID     uuid.UUID
		expErr         bool
		httpCodeExpect int
	}{
		{
			name:       "Valid token creation",
			courseUUID: courses[0].CourseUUID, // NOTE: такой курс должен уже быть создан заранее
			expErr:     false,
		},
		{
			name: "course which are not exist",
			courseUUID: func() uuid.UUID {
				id, err := uuid.NewRandom()
				if err != nil {
					t.Log("fail to generate uuid, need to rerun test")
					panic(err)
				}
				return id
			}(),
			expErr:         true,
			httpCodeExpect: http.StatusBadRequest,
		},
	}
	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			_, err := createApplication(tc.courseUUID, token)
			if tc.expErr {
				var netStatus ErrHttp
				errors.As(err, &netStatus)
				require.Error(t, err, tc.name)
				require.Equal(t, tc.httpCodeExpect, netStatus.getCode(), tc.name)
				return
			} else {
				require.NoError(t, err, tc.name)
				return
			}

		})
	}
}

func TestGetCourses(t *testing.T) {
	jwtResp, err := signIn(LoginRequest{Email: "jaba@jaba.jaba", Password: "jaba"})
	require.NoError(t, err, "failed to get token")
	token := jwtResp.Token
	t.Log("attempt to create an application with token: " + token)
	_, err = getAllCourses()
	require.NoError(t, err)
}

func generateNewUserAndToken(email, password string) (string, error) {
	app, err := signUp(
		RegistrationBody{
			FirstName:   email,
			LastName:    email,
			Email:       email + "@gmail.com",
			Password:    password,
			PhoneNumber: "+78005553535",
		},
	)
	if err != nil {
		return "", err
	}
	return app.Token, nil
}

func generateToken(email, password string) (string, error) {
	jwtToken, err := signIn(LoginRequest{
		Email:    email,
		Password: password,
	})
	if err != nil {
		return "", err
	}
	return jwtToken.Token, nil
}

func getExistCourseUUID() uuid.UUID {
	res, err := getAllCourses()
	if err != nil {
		return uuid.UUID{}
	}
	if len(res) == 0 {
		return uuid.UUID{}
	}

	return res[0].CourseUUID
}

func TestUpdateApplicationStatus(t *testing.T) {
	testCases := []struct {
		name              string
		username          string
		password          string
		courseUUID        uuid.UUID // function that create new user and register him for course
		applicationStatus string
		expErr            bool
		httpCodeExpect    int
	}{
		{
			name:              "total valid application request",
			username:          uuid.NewString(),
			password:          uuid.NewString(),
			courseUUID:        getExistCourseUUID(),
			applicationStatus: "OK",
			expErr:            false,
		},
		{
			name:              "invalid status",
			username:          uuid.NewString(),
			password:          uuid.NewString(),
			courseUUID:        getExistCourseUUID(),
			applicationStatus: "adslls",
			expErr:            true,
		},
		{
			name:              "reject status",
			username:          uuid.NewString(),
			password:          uuid.NewString(),
			courseUUID:        getExistCourseUUID(),
			applicationStatus: "REJECT",
			expErr:            false,
		},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			token, err := generateNewUserAndToken(tc.username, tc.password)
			require.NoError(t, err)

			applicationID, err := createApplication(tc.courseUUID, token)
			require.NoError(t, err)

			err = updateApplicationStatus(applicationID, token, tc.applicationStatus)
			if tc.expErr {
				require.Error(t, err, tc.name)
			} else {
				require.NoError(t, err, tc.name)
			}
		})
	}

	t.Run("send new status to same application", func(t *testing.T) {
		username := uuid.NewString()
		pass := uuid.NewString()
		token, err := generateNewUserAndToken(username, pass)
		require.NoError(t, err)

		courseUUID := getExistCourseUUID()
		applicationID, err := createApplication(courseUUID, token)
		require.NoError(t, err)
		err = updateApplicationStatus(applicationID, token, "OK")
		require.NoError(t, err)
		err = updateApplicationStatus(applicationID, token, "OK")
		require.Error(t, err)
		err = updateApplicationStatus(applicationID, token, "REJECT")
		require.Error(t, err)
	})
}

func TestGetSpecificCourse(t *testing.T) {
	testCases := []struct {
		name           string
		username       string
		password       string
		courseUUID     uuid.UUID // function that create new user and register him for course
		expErr         bool
		httpCodeExpect int
	}{
		{
			name:       "total valid application request",
			username:   uuid.NewString(),
			password:   uuid.NewString(),
			courseUUID: getExistCourseUUID(),
			expErr:     false,
		},
		{
			name:       "course which are not exist",
			username:   uuid.NewString(),
			password:   uuid.NewString(),
			courseUUID: uuid.MustParse("11129969-9cc4-4b70-9da4-17df37be70fa"),
			expErr:     true,
		},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			token, err := generateNewUserAndToken(tc.username, tc.password)
			require.NoError(t, err)

			course, err := getCourse(token, tc.courseUUID)

			if tc.expErr {
				require.Error(t, err, tc.name)
			} else {
				require.NoError(t, err, tc.name)
				require.NotNil(t, course)
			}
		})
	}
}

func TestCreateCourse(t *testing.T) {

	testCases := []struct {
		name   string
		course Course
		expErr bool
	}{
		{
			name: "successful creation",
			course: Course{
				CourseName:     "Random bullshit from skillbox",
				CoursePrice:    2341,
				Description:    "cool text of bullshit",
				TopicName:      "MARKETING",
				CourseDuration: 23,

				WithJobOffer: false,
			},
			expErr: false,
		},
	}
	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			token, err := generateToken("admin", "admin")

			require.NoError(t, err)

			err = createCourse(token, tc.course)
			if tc.expErr {
				require.Error(t, err)
			} else {
				require.NoError(t, err)
			}
		})
	}
}

func TestDeleteCourse(t *testing.T) {
	//	нужно сделать так, чтобы он
}
