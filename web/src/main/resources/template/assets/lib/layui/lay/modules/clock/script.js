layui.define(["jquery", "moment"], function(exports) {
	var jQuery = layui.jquery;
	$(function() {
		// Cache some selectors
		var clock = $('#clock'),
			alarm = clock.find('.alarm'),
			ampm = clock.find('.ampm'),
			weekType = clock.find('.weekType'),
			dialog = $('#alarm-dialog').parent(),
			alarm_set = $('#alarm-set'),
			alarm_clear = $('#alarm-clear'),
			time_is_up = $('#time-is-up').parent();
		
		function getWeekOfYear() {
			var today = new Date();
			var firstDay = new Date(today.getFullYear(), 0, 1);
			var dayOfWeek = firstDay.getDay();
			var spendDay = 1;
			if(dayOfWeek != 0) {
				spendDay = 7 - dayOfWeek + 1;
			}
			firstDay = new Date(today.getFullYear(), 0, 1 + spendDay);
			var d = Math.ceil((today.valueOf() - firstDay.valueOf()) / 86400000);
			var result = Math.ceil(d / 7);
			return result + 1;
		}
		if(getWeekOfYear() % 2 == 0){
			weekType.html('双周');
		} else {
			weekType.html('单周');
		}
			
		// This will hold the number of seconds left
		// until the alarm should go off
		var alarm_counter = -1;
		// Map digits to their names (this will be an array)
		var digit_to_name = 'zero one two three four five six seven eight nine'.split(' ');
		// This object will hold the digit elements
		var digits = {};
		// Positions for the hours, minutes, and seconds
		var positions = [
			'h1', 'h2', ':', 'm1', 'm2', ':', 's1', 's2'
		];
		// Generate the digits with the needed markup,
		// and add them to the clock
		var digit_holder = clock.find('.digits');
		$.each(positions, function() {
			if(this == ':') {
				digit_holder.append('<div class="dots">');
			} else {
				var pos = $('<div>');
				for(var i = 1; i < 8; i++) {
					pos.append('<span class="d' + i + '">');
				}
				// Set the digits as key:value pairs in the digits object
				digits[this] = pos;
				// Add the digit elements to the page
				digit_holder.append(pos);
			}
		});
		// Add the weekday names
		var weekday_names = '周一 周二 周三 周四 周五 周六 周日'.split(' '),
			weekday_holder = clock.find('.weekdays');
		$.each(weekday_names, function() {
			weekday_holder.append('<span>' + this + '</span>');
		});
		var weekdays = clock.find('.weekdays span');
		// Run a timer every second and update the clock
		(function update_time() {
			// Use moment.js to output the current time as a string
			// hh is for the hours in 12-hour format,
			// mm - minutes, ss-seconds (all with leading zeroes),
			// d is for day of week and A is for AM/PM
			var now = moment().format("hhmmssdA");
			digits.h1.attr('class', digit_to_name[now[0]]);
			digits.h2.attr('class', digit_to_name[now[1]]);
			digits.m1.attr('class', digit_to_name[now[2]]);
			digits.m2.attr('class', digit_to_name[now[3]]);
			digits.s1.attr('class', digit_to_name[now[4]]);
			digits.s2.attr('class', digit_to_name[now[5]]);
			// The library returns Sunday as the first day of the week.
			// Stupid, I know. Lets shift all the days one position down, 
			// and make Sunday last
			var dow = now[6];
			dow--;
			// Sunday!
			if(dow < 0) {
				// Make it last
				dow = 6;
			}
			// Mark the active day of the week
			weekdays.removeClass('active').eq(dow).addClass('active');
			// Set the am/pm text:
			ampm.text(now[7] + now[8]);
			// Is there an alarm set?
			if(alarm_counter > 0) {
				// Decrement the counter with one second
				alarm_counter--;
				// Activate the alarm icon
				alarm.addClass('active');
			} else if(alarm_counter == 0) {
				time_is_up.fadeIn();
				// Play the alarm sound. This will fail
				// in browsers which don't support HTML5 audio
				try {
					$('#alarm-ring')[0].play();
				} catch(e) {}
				alarm_counter--;
				alarm.removeClass('active');
			} else {
				// The alarm has been cleared
				alarm.removeClass('active');
			}
			// Schedule this function to be run again in 1 sec
			setTimeout(update_time, 1000);
		})();
		// Switch the theme
		$('#switch-theme').click(function() {
			clock.toggleClass('light dark');
		});
		// Handle setting and clearing alamrs
		$('.alarm-button').click(function() {
			// Show the dialog
			dialog.trigger('show');
		});
		dialog.find('.close').click(function() {
			dialog.trigger('hide')
		});
		dialog.click(function(e) {
			// When the overlay is clicked, 
			// hide the dialog.
			if($(e.target).is('.overlay')) {
				// This check is need to prevent
				// bubbled up events from hiding the dialog
				dialog.trigger('hide');
			}
		});
		alarm_set.click(function() {
			var valid = true,
				after = 0,
				to_seconds = [3600, 60, 1];
			dialog.find('input').each(function(i) {
				// Using the validity property in HTML5-enabled browsers:
				if(this.validity && !this.validity.valid) {
					// The input field contains something other than a digit,
					// or a number less than the min value
					valid = false;
					this.focus();
					return false;
				}
				after += to_seconds[i] * parseInt(parseInt(this.value));
			});
			if(!valid) {
				alert('Please enter a valid number!');
				return;
			}
			if(after < 1) {
				alert('Please choose a time in the future!');
				return;
			}
			alarm_counter = after;
			dialog.trigger('hide');
		});
		alarm_clear.click(function() {
			alarm_counter = -1;
			dialog.trigger('hide');
		});
		// Custom events to keep the code clean
		dialog.on('hide', function() {
			dialog.fadeOut();
		}).on('show', function() {
			// Calculate how much time is left for the alarm to go off.
			var hours = 0,
				minutes = 0,
				seconds = 0,
				tmp = 0;
			if(alarm_counter > 0) {
				// There is an alarm set, calculate the remaining time
				tmp = alarm_counter;
				hours = Math.floor(tmp / 3600);
				tmp = tmp % 3600;
				minutes = Math.floor(tmp / 60);
				tmp = tmp % 60;
				seconds = tmp;
			}
			// Update the input fields
			dialog.find('input').eq(0).val(hours).end().eq(1).val(minutes).end().eq(2).val(seconds);
			dialog.fadeIn();
		});
		time_is_up.click(function() {
			time_is_up.fadeOut();
		});
	});
	layui.link(basePath + '../../lib/layui/lay/modules/clock/style.css');
	exports('clock', null);
});