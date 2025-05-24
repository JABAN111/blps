package data

import (
	"errors"
	"github.com/google/uuid"
	"github.com/stretchr/testify/require"
	"math/rand"
	"net/http"
	"testing"
)

func TestRegistrationWithoutCourseID(t *testing.T) {
	reg := RegistrationBody{
		FirstName:   "Ivan",
		LastName:    "Ivanov",
		Email:       uuid.NewString() + "@gm.gm",
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
				Email:    "admin@admin.admin",
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
				require.NoError(t, err)
				require.NotNil(t, res, tc.name)
				require.True(t, len(res.Token) > 5)
			}
		})
	}
}

func TestCreateApplication(t *testing.T) {
	jwtResp, err := signIn(LoginRequest{Email: "admin@admin.admin", Password: "admin"})
	require.NoError(t, err, "failed to get token")
	token := jwtResp.Token
	t.Log("attempt to create an application with token: " + token)

	courses, err := getAllCourses()
	require.NoError(t, err, "failed to get courses")
	require.True(t, len(courses) != 0, "at least one course must be")

	testCases := []struct {
		name           string
		courseID       int
		expErr         bool
		httpCodeExpect int
	}{
		{
			name:     "Valid token creation",
			courseID: courses[0].CourseID, // NOTE: такой курс должен уже быть создан заранее
			expErr:   false,
		},
		{
			name:           "course which are not exist",
			courseID:       (rand.Int() + rand.Int()) * 400,
			expErr:         true,
			httpCodeExpect: http.StatusBadRequest,
		},
	}
	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			_, err := createApplication(tc.courseID, token)
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

func TestUpdateApplicationStatus(t *testing.T) {
	testCases := []struct {
		name              string
		username          string
		password          string
		courseID          int // function that create new user and register him for course
		applicationStatus string
		expErr            bool
		httpCodeExpect    int
	}{
		{
			name:              "total valid application request",
			username:          uuid.NewString(),
			password:          uuid.NewString(),
			courseID:          getExistCourseID(),
			applicationStatus: "OK",
			expErr:            false,
		},
		{
			name:              "invalid status",
			username:          uuid.NewString(),
			password:          uuid.NewString(),
			courseID:          getExistCourseID(),
			applicationStatus: "adslls",
			expErr:            true,
		},
		{
			name:              "reject status",
			username:          uuid.NewString(),
			password:          uuid.NewString(),
			courseID:          getExistCourseID(),
			applicationStatus: "REJECT",
			expErr:            false,
		},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			token, err := generateNewUserAndToken(tc.username, tc.password)
			require.NoError(t, err)

			applicationID, err := createApplication(tc.courseID, token)
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

		courseUUID := getExistCourseID()
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
