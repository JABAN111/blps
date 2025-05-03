package data

import (
	"github.com/stretchr/testify/require"
	"testing"
)

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
			token, err := generateToken("admin@admin.admin", "admin")

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

}
