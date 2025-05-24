package data

import (
	"github.com/google/uuid"
	"github.com/stretchr/testify/require"
	"math/rand"
	"testing"
)

func TestGetCourses(t *testing.T) {
	jwtResp, err := signIn(LoginRequest{Email: "admin@admin.admin", Password: "admin"})
	require.NoError(t, err, "failed to get token")
	token := jwtResp.Token
	t.Log("attempt to create an application with token: " + token)
	_, err = getAllCourses()
	require.NoError(t, err)
}

func TestGetSpecificCourse(t *testing.T) {
	testCases := []struct {
		name           string
		username       string
		password       string
		courseID       int // function that create new user and register him for course
		expErr         bool
		httpCodeExpect int
	}{
		{
			name:     "total valid application request",
			username: uuid.NewString(),
			password: uuid.NewString(),
			courseID: getExistCourseID(),
			expErr:   false,
		},
		{
			name:     "course which are not exist",
			username: uuid.NewString(),
			password: uuid.NewString(),
			courseID: rand.Int() * 100,
			expErr:   true,
		},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			//token, err := generateNewUserAndToken(tc.username, tc.password)
			//require.NoError(t, err)

			course, err := getCourse(tc.courseID)

			if tc.expErr {
				require.Error(t, err, tc.name)
			} else {
				require.NoError(t, err, tc.name)
				require.NotNil(t, course)
			}
		})
	}
}
